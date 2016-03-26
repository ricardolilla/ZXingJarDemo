package jim.h.common.android.zxingjar.demo;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.text.DecimalFormat;
import java.util.logging.SimpleFormatter;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;

public class Teste extends Activity {

    TabHost tabHost;

    private XYPlot aprHistoryPlot = null;
    private SimpleXYSeries pressHistorySeries = null;
    private double pressao;
    private LeituraParam leituraParam;
    Thread t;
    Button buttonSMS;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        df=new DecimalFormat("0.00");

        buttonSMS=(Button) findViewById(R.id.buttonSMS);
        buttonSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage("011981473453",null,"Pressao:" + df.format(pressao),null,null);
            }
        });

        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
        pressHistorySeries = new SimpleXYSeries("Pressão");
        pressHistorySeries.useImplicitXVals();

        aprHistoryPlot.setRangeBoundaries(0, 250, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, 5, BoundaryMode.FIXED);
        LineAndPointFormatter linPressao=new LineAndPointFormatter(Color.YELLOW,null,null,null);
        linPressao.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        aprHistoryPlot.addSeries(pressHistorySeries, linPressao);

        aprHistoryPlot.setDomainStepValue(5);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Gráfico");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Pressão (bar)");
        aprHistoryPlot.getRangeLabelWidget().pack();


        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pressão");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tab Two");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Tab Three");
        host.addTab(spec);

        leituraParam=new LeituraParam();
        t=new Thread(leituraParam);
        t.start();
    }



    class LeituraParam implements Runnable{

        @Override
        public void run() {
            while(true){
                try{
                    Thread.sleep(200);
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                }
                pressao = Math.random() * 250;
                if(pressHistorySeries.size() >= 30){
                    pressHistorySeries.removeFirst();
                }
                pressHistorySeries.addLast(null,pressao);
                aprHistoryPlot.redraw();
            }
        }
    }
}
