package com.example.cardview

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
    fun layout_add(view: View){

        //スクロールビューのifを取得しビューグループに変換
        val SV:ViewGroup=view.findViewById<View>(R.id.sv) as ViewGroup

        //削除の場合はコメントを消す（後で調べなくていいように敢えて書き残します）
        //SV.removeAllViews()

        //レイアウトデータを読み込み追加する
        getLayoutInflater().inflate(R.layout.cardpage, SV)

        //今回追加したレイアウトのデータを取得
        val layout:ConstraintLayout=SV.getChildAt(count)as ConstraintLayout

        //レイアウトにタグをつけて後で扱いやすくする
        layout.setTag(count)


        //カウントを追加する
        count++

    }

}