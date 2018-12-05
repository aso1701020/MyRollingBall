package jp.ac.asojuku.st.myrollingball

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
        , SensorEventListener, SurfaceHolder.Callback {

    // 描画フラグ
    private var flagg:Boolean = true;

    //プロパティ
    private var surfaceWidth: Int = 0; //幅
    private var surfaceHeight: Int = 0; //高さ

    private val radius = 50.0f;
    private val coef = 1000.0f;

    private var ballX: Float = 0f;
    private var ballY: Float = 0f;
    private var vx: Float = 0f;
    private var vy: Float = 0f;
    private var time: Long = 0L;
    private  var  isTouched:Boolean = false;

    //誕生時
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder; //サーフェスホルダーを取得
        holder.addCallback(this);//コールバック
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
         btn_reset.setOnClickListener{
            reset();
        }
    }

    //画面表示・歳表示のライフサイクルイベント
    override fun onResume() {
        //親クラスのonResume()処理
        super.onResume()
//        //自クラスのonResume処理
//        //センサーマネージャをOSから取得
//        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        //センサーマネージャから加速度センサー
//        val accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        //
//        sensorManager.registerListener(
//                this, //イベントリスナー機能を持つインスタンス
//                accSensor, //
//                SensorManager.SENSOR_DELAY_GAME //
//        )
    }

    //画面が非表示の時のライフサイクルイベント
    override fun onPause() {
        super.onPause()
//        //センサーマネージャを取得
//        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager;
//        //
//        sensorManager.unregisterListener(this)
    }

    //精度が変わった時のイベントコールバック
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    //センサーの値が変わった時のイベントコールバック
    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return; }
//        //
//        //
//        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
//            val str = "x = ${event.values[0].toString()}" +
//                    ",y = ${event.values[1].toString()}" +
//                    ",z = ${event.values[2].toString()}"
//            //Log.d("加速度センサ",str);
//            //テキストビューに表示
//            txvMain.text = str;
        //}
        if (time == 0L) {
            time = System.currentTimeMillis(); }
        //イベントの情報がアクセラメーター
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0] * -1;
            val y = event.values[1];

            //
            var t = (System.currentTimeMillis() - time).toFloat();
            time = System.currentTimeMillis();
            t /= 1000.0f;

            //
            val dx = (vx * t) + (x * t * t) / 2.0f;
            val dy = (vy * t) + (y * t * t) / 2.0f;
            ballX += (dx * coef)
            ballY += (dy * coef)
            vx += (x * t);
            vy += (y * t);

            if (ballX - radius < 0 && vx < 0) {
                vx = -vx/1.5f
                ballX = radius
            } else if (ballX + radius > surfaceWidth && vx > 0) {
                vx = -vx/1.5f
                ballX = surfaceWidth - radius
            }
            if (ballY - radius < 0 && vy < 0) {
                vy = -vy/1.5f
                ballY = radius
            } else if (ballY + radius > surfaceHeight && vy > 0) {
                vy = -vy/1.5f
                ballY = surfaceHeight - radius
            }
            if(flagg){
                if(ballX - radius <= 850f && 700f <= ballY + radius && ballY - radius <= 800f){
                    val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                            as SensorManager;
                    //
                    //sensorManager.unregisterListener(this);
                    flagg = false;
                    hantei(false);
                }
                if(300f <= ballX + radius && ballX - radius <= 1150f && 1100f <= ballY + radius && ballY - radius <= 1200){
                    val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                            as SensorManager;
                    //
                    //sensorManager.unregisterListener(this);
                    flagg = false;
                    hantei(false);
                }
                if(0f <= ballX + radius && ballX - radius <= 200f && 0f <= ballY + radius && ballY - radius <= 200f){
                    flagg = false;
                    hantei(true);
                }

                //キャンパスに描画
                if(flagg){
                    this.drawCanvas()
                }
            }
        }
    }

    //ボールの描画の計算処理


    //サーフェスが更新された時のイベント
    override fun surfaceChanged(p0: SurfaceHolder?, format: Int,
                                width: Int, height: Int) {
        surfaceWidth = width;
        surfaceHeight = height;
        //ボールの初期値を保存
        ballX = (width).toFloat();
        ballY = (height).toFloat();

    }

    //サーフェスが破棄された時のイベント
    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        //加速度センサーの登録を解除する流れ
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager;
        //
        sensorManager.unregisterListener(this);
    }

    //サーフェスが作成された時のイベント
    override fun surfaceCreated(holder: SurfaceHolder?) {
        val sensorManager = this.getSystemService(Context.SENSOR_SERVICE)
                as SensorManager;
        val accSensor =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //
        sensorManager.registerListener(
                this,
                accSensor,
                SensorManager.SENSOR_DELAY_GAME
        )
    }

    private fun drawCanvas() {
        //キャンバスをロックして取得
        val canvas = surfaceView.holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        //キャンバスに円を描いてボールにする
        canvas.drawCircle(ballX, ballY, radius,
                Paint().apply {
                    color = Color.RED;
                });

        canvas.drawRect(0f,700f,850f,800f,
                Paint().apply {
                    color=Color.GREEN
                })

        canvas.drawRect(300f,1100f,1150f,1200f,
                Paint().apply {
                    color=Color.GREEN
                })
        // ゴール
        canvas.drawRect(0f,0f,200f,200f,
                Paint().apply {
                    color=Color.BLUE
                })

        surfaceView.holder.unlockCanvasAndPost(canvas);

    }
    private fun reset(){
        ballX = surfaceWidth - radius;
        ballY = surfaceHeight - radius;
        vx = 0f;
        vy = 0f;
        flagg = true;
        icon.setImageResource(R.mipmap.ic_launcher)
        hantei.setText(R.string.ouen)
    }

    private  fun hantei(kekka: Boolean){
        if(kekka) {
            hantei.setText("おめでとう！")
            icon.setImageResource(R.drawable.neko1)
        }else{
            hantei.setText("残念！")
            icon.setImageResource(R.drawable.neko2)
        }
    }
}