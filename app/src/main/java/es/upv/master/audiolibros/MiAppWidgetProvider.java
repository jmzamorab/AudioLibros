package es.upv.master.audiolibros;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import es.upv.master.audiolibros.singletons.LibrosSingleton;

/**
 * Created by JoseMaria.Zamora on 24/01/2017.
 */

public class MiAppWidgetProvider extends AppWidgetProvider {
    //private static Aplicacion app;
    //private Aplicacion app;
    //private LibrosSingleton librosSingleton;


    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] widgetIds) {
        //app = ((Aplicacion) (context.getApplicationContext()));
        //librosSingleton = LibrosSingleton.getInstance(context);
        for (int widgetId : widgetIds) {
            // TODO creo metodo que me muestre titulo y autor de último libro visto
            // obviamente hay que controlar que SharedPreferences tenga contenido o que muestre el primero.

            actualizaWidget(context, widgetId);
        }
    }

    public static void actualizaWidget(Context context, int widgetId) {
        Libro item;
        LibrosSingleton librosSingleton = LibrosSingleton.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        LibroStorage libroStorage = LibrosSharedPreferenceStorage.getInstance(context);
        if (libroStorage.hasLastBook())
        {
            int id = libroStorage.getLastBook();
            item = librosSingleton.getVectorLibros().get(id);
        }
        else
        {
            item = librosSingleton.getVectorLibros().get(0);
        }
        /*SharedPreferences pref = context.getSharedPreferences("es.upv.master.audiolibros_internal", MODE_PRIVATE);
        int id = pref.getInt("ultimo", -1);
        if (id >= 0) {
            item = app.getAdaptador().getItem(id);
        } else {
            item = app.getAdaptador().getItem(0);
        }*/
        remoteViews.setTextViewText(R.id.widgetautor, item.getAutor());
        remoteViews.setTextViewText(R.id.widgettitulo, item.getTitulo());

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widgettitulo, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.widgetautor, pendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
    }
}
