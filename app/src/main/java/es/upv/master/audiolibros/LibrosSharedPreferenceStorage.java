package es.upv.master.audiolibros;

import android.content.Context;
import android.content.SharedPreferences;

import es.upv.master.audiolibros.Interactors.SaveLastBook;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class LibrosSharedPreferenceStorage implements LibroStorage, UserStorage {
    public static final String PREF_AUDIOLIBROS = "es.upv.master.audiolibros_internal";
    public static final String KEY_ULTIMO_LIBRO = "ultimo";
    public static final String KEY_PROVIDER = "provider";
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_SECOND = "segundos";
    private final Context context;
    private SaveLastBook saveLastBook;

    private static LibrosSharedPreferenceStorage instance;

    private LibrosSharedPreferenceStorage(Context context)
    {
        this.context = context;
    }

    public static LibroStorage  getInstance(Context context)
    {
      if (instance == null)
      {
          instance = new LibrosSharedPreferenceStorage(context);
      }
        return instance;
    }

     @Override
    public boolean hasLastBook()
    {
        return getPreference().contains(KEY_ULTIMO_LIBRO);
    }

    private SharedPreferences getPreference(){
        return context.getSharedPreferences(PREF_AUDIOLIBROS, Context.MODE_PRIVATE);
    }

    @Override
    public int getLastBook(){
        return getPreference().getInt(KEY_ULTIMO_LIBRO, -1);
    }

    @Override
    public void setLastBook(int lastBook) {
        SharedPreferences pref = getPreference();
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(KEY_ULTIMO_LIBRO, lastBook);
        editor.apply();
    }

    @Override
    public void saveLastBook(int lastBook) {
        setLastBook(lastBook);
    }


    @Override
    public boolean hasEMail() {
        return getPreference().contains(KEY_EMAIL);
    }

    @Override
    public String getProvider() {
        return getPreference().getString(KEY_PROVIDER, "NO Provider");
    }

    @Override
    public void setProvider(String provider) {
        SharedPreferences pref = getPreference();
        pref.edit().putString(KEY_PROVIDER, provider).apply();
    }

    @Override
    public String getName() {
        return getPreference().getString(KEY_NAME, "NO Name");
    }

    @Override
    public void setName(String name) {
        SharedPreferences pref = getPreference();
        pref.edit().putString(KEY_NAME, name).apply();
    }

    @Override
    public void setEMail(String eMail) {
        SharedPreferences pref = getPreference();
        pref.edit().putString(KEY_EMAIL, eMail).apply();

    }

    @Override
    public String getEmail() {
        return getPreference().getString(KEY_NAME, "NO EMail");
    }


    @Override
    public void remove(String key) {
        SharedPreferences pref = getPreference();
        pref.edit().remove(key);
    }
}
