package es.upv.master.audiolibros;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upv.master.audiolibros.Interactors.GetLastBook;
import es.upv.master.audiolibros.Interactors.HasLastBook;
import es.upv.master.audiolibros.Interactors.SaveLastBook;
import es.upv.master.audiolibros.Presenter.MainPresenter;
import es.upv.master.audiolibros.Repositories.BooksRepository;
import es.upv.master.audiolibros.singletons.FirebaseAuthSingleton;
import es.upv.master.audiolibros.singletons.LibrosSingleton;
import es.upv.master.audiolibros.fragments.DetalleFragment;
import es.upv.master.audiolibros.fragments.PreferenciasFragment;
import es.upv.master.audiolibros.fragments.SelectorFragments;
import es.upv.master.audiolibros.singletons.VolleySingleton;

import static android.R.attr.id;
import static es.upv.master.audiolibros.R.id.txtName;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorLibrosFiltro adaptador;
    private AppBarLayout appBarLayout;
    private TabLayout tabs;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private MainPresenter mainPresenter;
    private LibrosSingleton librosSingleton;
    private LibroStorage libroStorage;
    private UserStorage userStorage;
    private FirebaseAuth auth;
    VolleySingleton volleySingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_bkp);
        librosSingleton = LibrosSingleton.getInstance(this);
        libroStorage = LibrosSharedPreferenceStorage.getInstance(this);
        volleySingleton = VolleySingleton.getInstance(this);

        BooksRepository booksRepository = new BooksRepository(libroStorage);
        mainPresenter = new MainPresenter(new SaveLastBook(booksRepository), new HasLastBook(booksRepository), new GetLastBook(booksRepository), this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.clickFavoriteButton();
            }
        });
        //Añadido para mostrar ReciclerView
        adaptador = (AdaptadorLibrosFiltro) librosSingleton.getAdaptador();
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?
                R.id.contenedor_pequeno : R.id.contenedor_izquierdo;
        SelectorFragments primerFragment = new SelectorFragments();
        getFragmentManager().beginTransaction().add(idContenedor, primerFragment)
                .commit();
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Todos"));
        tabs.addTab(tabs.newTab().setText("Nuevos"));
        tabs.addTab(tabs.newTab().setText("Leidos"));
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                          @Override
                                          public void onTabSelected(TabLayout.Tab tab) {
                                              switch (tab.getPosition()) {
                                                  case 0: //Todos
                                                      adaptador.setNovedad(false);
                                                      adaptador.setLeido(false);
                                                      break;
                                                  case 1: //Nuevos
                                                      adaptador.setNovedad(true);
                                                      adaptador.setLeido(false);
                                                      break;
                                                  case 2: //Leidos
                                                      adaptador.setNovedad(false);
                                                      adaptador.setLeido(true);
                                                      break;
                                              }
                                              adaptador.notifyDataSetChanged();
                                          }

                                          @Override
                                          public void onTabUnselected(TabLayout.Tab tab) {
                                          }

                                          @Override
                                          public void onTabReselected(TabLayout.Tab tab) {
                                          }
                                      }

        );


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Navigation Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        TextView txtName = (TextView) headerLayout.findViewById(R.id.txtName);
        userStorage = (UserStorage) LibrosSharedPreferenceStorage.getInstance(this);
        txtName.setText(String.format(getString(R.string.welcome_message), userStorage.getName()));

        auth = FirebaseAuthSingleton.getInstance().getAuth();
        final FirebaseUser currentUser = auth.getCurrentUser();
        Uri urlImagen = currentUser.getPhotoUrl();
        if (urlImagen != null) {
            NetworkImageView fotoUsuario = (NetworkImageView) headerLayout.findViewById(R.id.imageView);
            fotoUsuario.setImageUrl(urlImagen.toString(), volleySingleton.getLectorImagenes());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void abrePreferencias() {
        int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?
                R.id.contenedor_pequeno : R.id.contenedor_izquierdo;
        PreferenciasFragment prefFragment = new PreferenciasFragment();
        getFragmentManager().beginTransaction()
                .replace(idContenedor, prefFragment)
                .addToBackStack(null)
                .commit();
    }


    //public void mostrarDetalle(int id) {
    public void mostrarDetalle(String key) {
        mainPresenter.openDetalle(key);
        //mainPresenter.openDetalle(id);
    }


    @Override
    public void mostrarNoUltimaVisita() {
        Toast.makeText(this, "Sin última vista", Toast.LENGTH_LONG).show();
    }

    @Override
    public void mostrarFragmentDetalle(String key) {
        DetalleFragment detalleFragment = (DetalleFragment) getFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(key);
        } else {
            DetalleFragment nuevoFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putString(DetalleFragment.ARG_ID_LIBRO, key);
            nuevoFragment.setArguments(args);
            FragmentTransaction transaccion = getFragmentManager().beginTransaction();
            transaccion.replace(R.id.contenedor_pequeno, nuevoFragment);
            transaccion.addToBackStack(null);
            transaccion.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_preferencias) {
            showPreferences();
            return true;
        } else if (id == R.id.menu_acerca) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showPreferences() {
        abrePreferencias();
    }

    public void irUltimoVisitado() {
        mainPresenter.clickFavoriteButton();
    }

    public void shareBook(Libro libro) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, libro.getTitulo());
        i.putExtra(Intent.EXTRA_TEXT, libro.getUrlAudio());
        startActivity(Intent.createChooser(i, "Compartir"));
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_todos:
                adaptador.setGenero("");
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_epico:
                adaptador.setGenero(Libro.G_EPICO);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_XIX:
                adaptador.setGenero(Libro.G_S_XIX);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_suspense:
                adaptador.setGenero(Libro.G_SUSPENSE);
                adaptador.notifyDataSetChanged();
                break;
            case R.id.nav_preferencias:
                showPreferences();
                break;
            case R.id.nav_signout:
                // ahora cierra de la nueva actividad personalizada
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userStorage.remove("provider");
                        userStorage.remove("email");
                        userStorage.remove("name");
                        Intent i = new Intent(MainActivity.this, CustomLoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                });
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void mostrarElementos(boolean mostrar) {
        appBarLayout.setExpanded(mostrar);
        toggle.setDrawerIndicatorEnabled(mostrar);
        if (mostrar) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            tabs.setVisibility(View.VISIBLE);
        } else {
            tabs.setVisibility(View.GONE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

}