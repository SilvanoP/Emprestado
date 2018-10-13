package macaxeira.com.emprestado.utils

import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import android.view.View
import macaxeira.com.emprestado.R

object Utils {

    @JvmStatic
    fun flipView(context: Context, back: View, front: View, showFront: Boolean) {
        val leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet
        val leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet
        val rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in) as AnimatorSet
        val rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out) as AnimatorSet

        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn.setTarget(back)
        rightOut.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut.setTarget(back)
        rightIn.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }

    @JvmStatic
    fun <T> fromSparseToList(sparse: SparseArray<T>): List<T> {
        val list = mutableListOf<T>()
        var index = 0
        while (index < sparse.size()) {
            list.add(sparse.valueAt(index))
            index++
        }

        return list.toList()
    }
}