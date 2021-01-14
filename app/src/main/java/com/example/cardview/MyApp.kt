package com.example.cardview

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.Base64
import android.widget.Toast
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*


class MyApp: Application(){

    //このコメント下にグローバル変数等記述、必要分のみをコメント付きで記述すること

    val CENTRAL_LATITUDE:Int=1304090//X座標側、下四桁が少数
    val CENTRAL_LONGITUDE:Int=335840//Y座標側、下四桁が小数

    var DIRECTORY:String?=null

    data class USERDATA(var ID:Int,var NAME:String,var ICON:Bitmap,var LENGTH:Int,var FAVORITE:Int,var COMMENT:String,var BACKGROUND:Int,var FRAME:Int,var STATE:Int)

    data class LOCAL_DC(var height:Float,var weight:Float,var TARGET: Int,var GPSFLG:Boolean,var HOME_X:Float,var HOME_Y:Float,var ACQUIED:Int,var MYCOLOR: Color)

    data class GPSDATA(var GPS_D:Date?,var GPS_X:Float?,var GPS_Y:Float?,var GPS_A:Float?,var GPS_S:Float?)

    data class MAPDATA(var MAP :Array<Array<Int?>>,var MAP_X:Float?,var MAP_Y:Float?)

    data class ACTIVITYDATA(var DATE:Date, var TARGET:Int,var STEP:Int,var DISTANCE:Int,var CAL:Int)

    data class CARDDATA(var ID:Int,var NAME:String,var ICON:Bitmap?,var LEVEL:Int,var DISTANCE:Int,var BADGE:Int,var BACKGROUND:Int,var FRAME:Int,var COMMENT:String,var STATE:Int)


    var GPS_LOG=mutableListOf<GPSDATA>()

    var ACTIVITY_LOG=mutableListOf<ACTIVITYDATA>()

    var GPS_BUF:GPSDATA=GPSDATA(null,null,null,null,null)

    var FRIENDLIST=mutableListOf<USERDATA>()

    var FAVORITELIST=mutableListOf<USERDATA>()

    //日本は経度122-154,緯度20-46に存在する
    //y320000,x260000のデータで成り立つ
    //500x500でバッファリングする

    //1単位当たり10mで計算

    //開始時処理
    override fun onCreate(){
        super.onCreate()
    }

    companion object{
        private var instance: MyApp?=null
        fun getInstance(): MyApp {
            if(instance ==null)
                instance = MyApp()
            return instance!!
        }
    }

    //名刺のBitmap画像を
    //CARDDATA型データを渡し、第二引数でresourcesを投げる
    fun CreateCardBitmap(DATA:CARDDATA,res:Resources): Bitmap {


        val img_card:Bitmap=BitmapFactory.decodeResource(res,R.drawable.card)
        val img_frame:Bitmap=FrameBitmapSearch(DATA.FRAME,res)
        val img_back:Bitmap=CardBackBitmapSearch(DATA.BACKGROUND,res)

        val img_icon:Bitmap?
        if(DATA.ICON!=null)img_icon=Bitmap.createScaledBitmap(DATA.ICON!!,(img_frame.height/13*5),(img_frame.height/13*5),true)
        else img_icon=null

        val img_badge_back=Bitmap.createScaledBitmap(BadgeBackBitmapSearch(DATA.BACKGROUND,res),(img_frame.height/5),(img_frame.height/5),true)
        val img_badge_icon=Bitmap.createScaledBitmap(BadgeIconBitmapSearch(DATA.BADGE,res),(img_frame.height/5),(img_frame.height/5),true)
        val img_level:Bitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res,R.drawable.levelframe),(img_frame.height/10*3),(img_frame.height/10*3),true)
        val str_id:String=DATA.ID.toString()
        val str_name:String=DATA.NAME
        val str_distance:String=DATA.DISTANCE.toString()
        val str_level:String=DATA.LEVEL.toString()
        val str_comment:String=DATA.COMMENT

        val width=img_card.width
        val height=img_card.height

        val frameWidth:Float=img_frame.width/12.54f

        val paint= Paint()
        val output= Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas= Canvas(output)

        paint.isAntiAlias=true

        canvas.drawBitmap(img_back,0f,0f,paint)

        canvas.drawBitmap(img_frame,0f,0f,paint)

        canvas.drawBitmap(img_card,0f,0f,paint)

        if(img_icon!=null)canvas.drawBitmap(img_icon,((width-frameWidth)/8f)-(img_icon.width/2f)+frameWidth/2+60,((height-frameWidth)/4f)-(img_icon.height/2f)+frameWidth/2+20,paint)

        canvas.drawBitmap(img_level,2510f,135f,paint)

        canvas.drawBitmap(img_badge_back,2610f,575f,paint)

        canvas.drawBitmap(img_badge_icon,2610f,575f,paint)


        paint.textSize=250f

        canvas.drawText(str_level,2805f-paint.measureText(str_level)/2,410f,paint)

        //※ビューは一度作ったものをリサイズして利用するので、位置は無理やりハードコートしています
        paint.textSize=150f

        canvas.drawText("ID：$str_id",(width-frameWidth)/4+frameWidth/2+125,(height-frameWidth)/4+frameWidth-250,paint)
        canvas.drawText(str_name,(width-frameWidth)/4+frameWidth/2+125,((height-frameWidth)/4f)+(paint.fontMetrics.top/-2) +frameWidth/2,paint)
        canvas.drawText(str_distance+"m",(width-frameWidth)/4*3+frameWidth/2-paint.measureText(str_distance+"m"),((height-frameWidth)/4f)+(paint.fontMetrics.top/-2) +frameWidth/2+200,paint)

        if(str_comment.length<=20){
            canvas.drawText(str_comment,width/2f-paint.measureText(str_comment)/2f,1475f,paint)
        }
        else if(str_comment.length<=40){
            canvas.drawText(str_comment.substring(0..19),width/2f-paint.measureText(str_comment.substring(0..19))/2f,1380f,paint)
            canvas.drawText(str_comment.substring(20),215f,1570f,paint)
        }



        paint.color= Color.parseColor("#808080")
        paint.strokeWidth=5f
        //canvas.drawLine(1000f,280f,2400f,280f,paint)
        canvas.drawLine(1000f,480f,2450f,480f,paint)
        canvas.drawLine(1000f,680f,2450f,680f,paint)
        canvas.drawLine(1000f,880f,2450f,880f,paint)

        return output
    }

    fun IconBitmapCreate(data:String?):Bitmap?{
        if(data==null)return null
        val decodedByte: ByteArray = Base64.decode(data, 0)
        val buf= BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        return buf
    }

    fun FrameBitmapSearch(ID:Int,res:Resources):Bitmap{
        var img = BitmapFactory.decodeResource(res, R.drawable.frame_1)
        when(ID){
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_5)
            6 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_6)
            7 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_7)
            8 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_8)
            9 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_9)
            10 -> img = BitmapFactory.decodeResource(res, R.drawable.frame_10)
        }
        return img
    }

    fun CardBackBitmapSearch(ID: Int,res: Resources):Bitmap{
        var img = BitmapFactory.decodeResource(res,R.drawable.card_background_1)
        when(ID){
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.card_background_5)
        }
        return img
    }


    fun BadgeBackBitmapSearch(ID:Int,res:Resources):Bitmap{
        var img = BitmapFactory.decodeResource(res, R.drawable.badge_background_0)
        when(ID){
            1 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_1)
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_background_4)
        }
        return img
    }

    fun BadgeIconBitmapSearch(ID:Int,res:Resources):Bitmap{
        var img:Bitmap=BitmapFactory.decodeResource(res, R.drawable.badge_icon_0)
        when(ID){
            1 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_1)
            2 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_2)
            3 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_3)
            4 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_4)
            5 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_5)
            6 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_6)
            7 -> img = BitmapFactory.decodeResource(res, R.drawable.badge_icon_7)
        }
        return img
    }

}

/*
GLOBALSETTING------------------------------------

->サーバーとの通信に使用するデータ

ID(int)
名前(string)
アイコン(string)
総距離(int)
お気に入りバッジ(int)
ひとこと(string)
名刺背景(int)
名刺フレーム(int)
バッジ状態(int)
-------------------------------------------------





LOCALSETTING-------------------------------------

->ユーザ情報等、アプリ内の設定を保存

身長(float)
体重(float)
目標歩数(int)
GPS取得設定(int)
自宅座標(float,float)
非取得範囲(int)
マイカラー(rgb)
-------------------------------------------------





STEPLOG------------------------------------------

->歩数データを保存。Fitnessはこのファイルを参照

日付(date)
目標歩数(int)
歩数(int)
消費カロリー(int)
-------------------------------------------------





GPSLOG-------------------------------------------

->GPSの全履歴を保存。書き込みはBUFと並列

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------





GPSBUF-------------------------------------------

->GPSのサーバー未送信分履歴を保存

日付(date)
時間(time)
経度(float)
緯度(float)
範囲(float)
速度(float)
-------------------------------------------------





FRIEND-------------------------------------------

->すれ違ったフレンドのIDを保存

ID(int)
-------------------------------------------------





FAVORITE-----------------------------------------

->お気に入りのユーザを保存

ID(int)
-------------------------------------------------





MAPLOG-------------------------------------------

->マップのバッファリングに使用（すると思われる)

-------------------------------------------------























名刺------------------------
・ID
・名前
・ユーザレベル
・アイコン
・総距離
・お気に入りバッジID
・ひとこと
・名刺背景色ID
・名刺フレームID
・バッジの状態（8桁の整数で管理）



バッチ処理時に通信する内容ーーーーーーーーーーーーー
・地図の変更（区間を経度緯度でx分割してフラグ管理、変更分のみアップデート）
 Lバイナリで管理する
・自分の現状の名刺データ

日付変更処理に通信する内容ーーーーーーーーーーーーー
・地図の更新

適宜更新時に取得する内容ーーーーーーーーーーーーーー
・他のユーザの名刺データ




アプリ終了時に処理
・地図更新情報
・すれちがい情報

変更時に処理
・ユーザ名刺データ

朝５時にバッチ処理
・地図更新情報
・ユーザ名刺データ
・すれちがい情報

起動時に処理
・


サーバで保持するべき情報ーーーーーーーーーーーーーー
・ID(8桁整数)
・名前(2バイト10文字）
・ユーザレベル(3桁整数）
・アイコン（300000文字)
・総距離(8桁整数）
・お気に入りバッジID(2桁整数）
・ひとこと（2バイト50文字)
・名刺背景色ID(2桁整数）
・名刺フレームID（2桁整数）
・バッジの状態（8桁の整数で管理）

・地図のマスタデータ200000x180000(csv)

各種ID----------------------------------------------

・バッジID

0ログイン日数
1レベル
2移動距離
3歩数
4開拓
5カロリー
6すれ違い
7イベント

・バッジ背景ID
0.Null
1.ブロンズ
2.シルバー
3.ゴールド
4.ダイヤ
------------------------------------------------------

  白　赤   青
|0123|456|789|


-------------------------------------------------------
 */