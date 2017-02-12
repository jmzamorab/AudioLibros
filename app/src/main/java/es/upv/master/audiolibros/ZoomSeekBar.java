package es.upv.master.audiolibros;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by padres on 21/01/2017.
 */

public class ZoomSeekBar extends View{// implements OnValueListener{
    // Valor a controlar
    private int val = 0; // valor seleccionado
    private int valMin = 0; // valor mínimo
    private int valMax = 0; // valor máximo
    private int escalaMin = 0; // valor mínimo visualizado
    private int escalaMax= 0; // valor máximo visualizado
    private int escalaIni= 0; // origen de la escala
    private int escalaRaya = 0; // cada cuantas unidades una rayas
    private int escalaRayaLarga = 0; // cada cuantas rayas una larga
    // Dimensiones en pixels
    private int altoNumeros;
    private int altoRegla;
    private int altoBar;
    private int altoPalanca;
    private int anchoPalanca;
    private int altoGuia;
    // Valores que indican donde dibujar
    private int xIni;
    private int yIni;
    private int ancho;
    // Objetos Rect con diferentes regiones
    private Rect escalaRect = new Rect();
    private Rect barRect = new Rect();
    private Rect guiaRect = new Rect();
    private Rect palancaRect = new Rect();
    // Objetos Paint globales para no tener que crearlos cada vez
    private Paint textoPaint = new Paint();
    private Paint reglaPaint = new Paint();
    private Paint guiaPaint = new Paint();
    private Paint palancaPaint = new Paint();
    //Vable guardamos diferencia entre alto deseado y alto asignado
    // si es mayor que cero, dbemos dividir el valo entre 2 para
    // que la vista se posicione a partir de esa altura, original
    // + la mitad de la diferencia entre alto dado por sistema y deseado
    private int difAlto = 0;
    private int yOrigen = 0;
    private OnValueListener listener;

    enum Estado {
        SIN_PULSACION, PALANCA_PULSADA, ESCALA_PULSADA, ESCALA_PULSADA_DOBLE
    }

    ;
    Estado estado = Estado.SIN_PULSACION;
    int antVal_0, antVal_1;

    public void setOnValueListener(OnValueListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
   // private boolean touchEv(MotionEvent event) {
        int x_0, y_0, x_1, y_1;
        x_0 = (int) event.getX(0);
        y_0 = (int) event.getY(0);
        int val_0 = getEscalaMin() + (x_0 - xIni) * (getEscalaMax() - getEscalaMin()) / ancho;
        if (event.getPointerCount() > 1)
        {
            x_1 = (int) event.getX(1);
            y_1 = (int) event.getY(1);
        } else

        {
            x_1 = x_0;
            y_1 = y_0;
        }

        int val_1 = getEscalaMin() + (x_1 - xIni) * (getEscalaMax() - getEscalaMin()) / ancho;
        switch (event.getAction() & MotionEvent.ACTION_MASK)

        {
            case MotionEvent.ACTION_DOWN:
                if (palancaRect.contains(x_0, y_0)) {
                    estado = Estado.PALANCA_PULSADA;
                } else if (barRect.contains(x_0, y_0)) {
                    if (val_0 > getVal()) {
                        setVal(getVal() + 1);
                    } else {
                        setVal(getVal()-1);
                    }
                    invalidate(barRect);
                } else if (escalaRect.contains(x_0, y_0)) {
                    estado = Estado.ESCALA_PULSADA;
                    antVal_0 = val_0;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (estado == Estado.ESCALA_PULSADA) {
                    if (escalaRect.contains(x_1, y_1)) {
                        antVal_1 = val_1;
                        estado = Estado.ESCALA_PULSADA_DOBLE;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                estado = Estado.SIN_PULSACION;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                    estado = Estado.ESCALA_PULSADA;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (estado == Estado.PALANCA_PULSADA) {
                    //val = ponDentroRango(val_0, escalaMin, escalaMax);
                    setVal(ponDentroRango(val_0, getEscalaMin(), getEscalaMax()));
                    //if (listener != null){
                    //listener.onChangeVal(String.valueOf(val));}
                    invalidate(barRect);
                }
                if (estado == Estado.ESCALA_PULSADA_DOBLE) {
                    setEscalaMin(antVal_0 + (xIni - x_0) * (antVal_0 - antVal_1) / (x_0 - x_1));
                    setEscalaMin(ponDentroRango(getEscalaMin(), getValMin(), getVal()));
                    setEscalaMax(antVal_0 + (ancho + xIni - x_0)
                            * (antVal_0 - antVal_1) / (x_0 - x_1));
                    setEscalaMax (ponDentroRango(getEscalaMax(), getVal(), getValMax()));
                    invalidate();
                }
                break;
        }

        return true;
    }


    int ponDentroRango(int valor, int minimo, int maximo) {
        if (valor < minimo) {
            return minimo;
        } else if (valor > maximo) {
            return maximo;
        } else {
            return valor;
        }
    }


    public ZoomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        //conectar.setOnClickListener(new OnClickListener()
        float dp = getResources().getDisplayMetrics().density;
        TypedArray attr = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ZoomSeekBar, 0, 0);
        try {
            altoNumeros = attr.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoNumeros, (int) (30 * dp));
            altoRegla = attr.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoRegla, (int) (20 * dp));
            altoBar = attr.getDimensionPixelSize(
                    R.styleable.ZoomSeekBar_altoBar, (int) (70 * dp));
            altoPalanca = attr.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoPalanca, (int) (40 * dp));
            altoGuia = attr.getDimensionPixelSize(R.styleable.ZoomSeekBar_altoGuia, (int) (10 * dp));
            anchoPalanca = attr.getDimensionPixelSize(R.styleable.ZoomSeekBar_anchoPalanca, (int) (20 * dp));
            textoPaint.setTextSize(attr.getDimension(R.styleable.ZoomSeekBar_altoTexto, 16 * dp));
            textoPaint.setColor(attr.getColor(R.styleable.ZoomSeekBar_colorTexto, Color.BLACK));
            reglaPaint.setColor(attr.getColor(R.styleable.ZoomSeekBar_colorRegla, Color.BLACK));
            guiaPaint.setColor(attr.getColor(R.styleable.ZoomSeekBar_colorGuia, Color.BLUE));
            palancaPaint.setColor(attr.getColor(
                    R.styleable.ZoomSeekBar_colorPalanca, 0xFF00007F));
        } finally {
            attr.recycle();
        }
        textoPaint.setAntiAlias(true);
        textoPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        xIni = getPaddingLeft();
        yIni = getPaddingTop();
        ancho = getWidth() - getPaddingRight() - getPaddingLeft();
        barRect.set(xIni, yIni, xIni + ancho, yIni + altoBar);
        escalaRect.set(xIni, yIni + altoBar, xIni + ancho, yIni + altoBar
                + altoNumeros + altoRegla);
        int y = yIni + (altoBar - altoGuia) / 2;
        yOrigen = y;
        guiaRect.set(xIni, y, xIni + ancho, y + altoGuia);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getEscalaMax() > getEscalaMin()) {
            // comprobamos si vable con diferencia de alturas tiene valor

            if (difAlto > 0) {
                yIni += difAlto / 2;
                // yIni += (altoBar - altoGuia) / 2;
                // guiaRect.set(xIni, yIni, xIni + ancho, yIni + altoGuia);
                int yNew = yIni + (altoBar - altoGuia) / 2;
                barRect.set(xIni, yIni, xIni + ancho, yIni + altoBar);
                guiaRect.set(xIni, yNew, xIni + ancho, yNew + altoGuia);
                difAlto = 0;
            }
// Dibujamos Barra con palanca
            canvas.drawRect(guiaRect, guiaPaint);
            int y = yIni + (altoBar - altoPalanca) / 2;
            // y += difAlto;
            //int x = xIni + ancho * (val - escalaMin) / (escalaMax - escalaMin) - anchoPalanca / 2;
            int x = xIni + ancho * (getVal() - getEscalaMin()) / (getEscalaMax() - getEscalaMin()) - anchoPalanca / 2;
            palancaRect.set(x, y, x + anchoPalanca, y + altoPalanca);
            canvas.drawRect(palancaRect, palancaPaint);
            palancaRect.set(x - anchoPalanca / 2, y, x + 3 * anchoPalanca / 2, y + altoPalanca);
// Dibujamos Escala
            int escalaInicial = getEscalaIni();
            while (escalaInicial <= getEscalaMax()) {
                if (escalaInicial >= getEscalaMin()) {
                    x = xIni + ancho * (escalaInicial - getEscalaMin()) / (getEscalaMax() - getEscalaMin());
                    if (((escalaInicial - getEscalaIni()) / getEscalaRaya()) % getEscalaRayaLarga() == 0) {
                        y = yIni + altoBar + altoRegla;
                        canvas.drawText(Integer.toString(escalaInicial), x, y + altoNumeros,
                                textoPaint);
                    } else {
                        y = yIni + altoBar + altoRegla * 1 / 3;
                    }
                    canvas.drawLine(x, yIni + altoBar, x, y, reglaPaint);
                }
                escalaInicial += getEscalaRaya();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int altoDeseado = altoNumeros + altoRegla + altoBar
                + getPaddingBottom() + getPaddingTop();
        int alto = obtenDimension(heightMeasureSpec, altoDeseado);
        difAlto = alto - altoDeseado;
        int anchoDeseado = 2 * altoDeseado;
        int ancho = obtenDimension(widthMeasureSpec, anchoDeseado);
        setMeasuredDimension(ancho, alto);
    }


    private int obtenDimension(int measureSpec, int deseado) {
        int dimension = MeasureSpec.getSize(measureSpec);
        int modo = MeasureSpec.getMode(measureSpec);
        if (modo == MeasureSpec.EXACTLY) {
            return dimension;
        } else if (modo == MeasureSpec.AT_MOST) {
            return Math.min(dimension, deseado);
        } else {
            return deseado;
        }
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        if (getValMin() <= val && val <= getValMax()) {
            this.val = val;
            if (listener != null){
                listener.onChangeVal(val);}
            setEscalaMin(Math.min(getEscalaMin(), val));
            setEscalaMax(Math.max(getEscalaMax(), val));
            invalidate();
        }
    }


    public int getValMin() {
        return valMin;
    }

    public void setValMin(int valMin) {
        this.valMin = valMin;
    }

    public int getValMax() {
        return valMax;
    }

    public void setValMax(int valMax) {
        this.valMax = valMax;
    }

    public int getEscalaMin() {
        return escalaMin;
    }

    public void setEscalaMin(int escalaMin) {
        this.escalaMin = escalaMin;
    }

    public int getEscalaMax() {
        return escalaMax;
    }

    public void setEscalaMax(int escalaMax) {
        this.escalaMax = escalaMax;
    }

    public int getEscalaIni() {
        return escalaIni;
    }

    public void setEscalaIni(int escalaIni) {
        this.escalaIni = escalaIni;
    }

    public int getEscalaRaya() {
        return escalaRaya;
    }

    public void setEscalaRaya(int escalaRaya) {
        this.escalaRaya = escalaRaya;
    }

    public int getEscalaRayaLarga() {
        return escalaRayaLarga;
    }

    public void setEscalaRayaLarga(int escalaRayaLarga) {
        this.escalaRayaLarga = escalaRayaLarga;
    }
}
