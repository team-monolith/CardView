package com.example.cardview

import android.app.AlertDialog
import android.content.Context.WINDOW_SERVICE
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    var count=0//レイアウト追加管理用

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fabボタンリスナー
        view.findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener{
            layout_add(view)
        }
    }

    //レイアウト追加用関数
    //Fragmentでの処理のためViewを引数として受け取る（findViewById等に利用するため）
    fun layout_add(view: View) {

        //スクロールビューのifを取得しビューグループに変換
        val SV: ViewGroup = view.findViewById<View>(R.id.sv) as ViewGroup

        //削除の場合はコメントを消す（後で調べなくていいように敢えて書き残します）
        //SV.removeAllViews()

        //レイアウトデータを読み込み追加する
        getLayoutInflater().inflate(R.layout.cardpage, SV)

        //今回追加したレイアウトのデータを取得
        val layout: ConstraintLayout = SV.getChildAt(count) as ConstraintLayout

        //レイアウトにタグをつけて後で扱いやすくする
        layout.setTag(count)

        (layout.findViewById<LinearLayout>(R.id.innnerlayout).getChildAt(1)as ImageView).setOnClickListener{
        }
        (layout.findViewById<LinearLayout>(R.id.innnerlayout).getChildAt(3)as ImageView).setOnClickListener{
        }
        (layout.findViewById<LinearLayout>(R.id.innnerlayout).getChildAt(5)as ImageView).setOnClickListener{
        }



            view.setOnClickListener {
                val vTag=view.getTag()
                if (vTag==1) {
                    val iv = ImageView(context)
                    iv.setImageResource(R.drawable.example2)
                    val FriendImageDialog = AlertDialog.Builder(activity)
                    FriendImageDialog.setTitle("test")
                            .setMessage("名刺イメージ")
                            .setView(iv)
                            .setPositiveButton("OK") { _, _ ->
                                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("キャンセル", null)
                            .show()

                }
                //ディスプレイサイズの取得
                /*val size = Point().also {
                    (context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.apply { getSize(it) }
                }
                val width = size.x
                val height = size.y*/




                Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
            }


        //カウントを追加する
        count++

    }

}