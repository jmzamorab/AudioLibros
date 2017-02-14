package es.upv.master.audiolibros;


public class OpenDetailClickAction implements ClickAction {
    private final MainActivity mainActivity;

    public OpenDetailClickAction(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void execute(String key) {
        mainActivity.mostrarDetalle(key);
    }
}
