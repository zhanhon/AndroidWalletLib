@file:JvmName("Router")

package com.ramble.ramblewallet.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.processphoenix.ProcessPhoenix
import com.ramble.ramblewallet.R
import com.ramble.ramblewallet.constant.REQUEST_CODE_1029
import com.ramble.ramblewallet.utils.Glide4Engine
import com.ramble.ramblewallet.utils.dimensionPixelSize
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import org.joor.Reflect

@JvmOverloads
fun Context.exit(restart: Boolean = false) {
    if (restart) {
        ProcessPhoenix.triggerRebirth(this.applicationContext)
    } else {
        Runtime.getRuntime().exit(0)
    }
}

fun dismiss(vararg targets: Any?) {
    if (targets.isEmpty()) return
    for (i in targets.indices) {
        val target = targets[i] ?: continue
        if (target is Dialog) {
            target.dismiss()
            continue
        }
        if (target is Toast) {
            target.cancel()
            continue
        }
        if (target is Snackbar) {
            target.dismiss()
            continue
        }
        if (target is PopupWindow) {
            target.dismiss()
            continue
        }
        if (target is PopupMenu) {
            target.dismiss()
            continue
        }
        if (target is android.widget.PopupMenu) {
            target.dismiss()
            continue
        }
        if (target is DialogFragment) {
            target.dismissAllowingStateLoss()
            continue
        }
        throw AssertionError()
    }
}

fun FragmentActivity.showDialogFragment(dialogFragment: DialogFragment) {
    showDialogFragment(
        this.supportFragmentManager,
        dialogFragment
    )
}

fun Fragment.showDialogFragment(dialogFragment: DialogFragment) {
    showDialogFragment(
        this.childFragmentManager,
        dialogFragment
    )
}

fun Activity.showAlertWhioutNo(title: String, message: String) {
    android.app.AlertDialog.Builder(this).setTitle(title)
        .setMessage(message)
        .setPositiveButton("确定") { dilog, which ->
            dilog.dismiss()
        }
        .show()

}

fun FragmentActivity.showDialogFragmentAllowingStateLoss(dialogFragment: DialogFragment) {
    showDialogFragmentAllowingStateLoss(
        this.supportFragmentManager,
        dialogFragment
    )
}

fun Fragment.showDialogFragmentAllowingStateLoss(dialogFragment: DialogFragment) {
    showDialogFragmentAllowingStateLoss(
        this.childFragmentManager,
        dialogFragment
    )
}

fun FragmentActivity.addFragment(fragment: Fragment, isAddBack: Boolean, @IdRes container: Int) {
    addFragment(this.supportFragmentManager, fragment, isAddBack, container)
}

fun FragmentActivity.replaceFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int
) {
    replaceFragment(this.supportFragmentManager, fragment, isAddBack, container)
}

fun FragmentActivity.forceReplaceFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int
) {
    forceReplaceFragment(this.supportFragmentManager, fragment, isAddBack, container)
}

fun FragmentActivity.forceAddFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int,
    tag: String
) {
    forceAddFragment(this.supportFragmentManager, fragment, isAddBack, container, tag)
}

fun FragmentActivity.forceReplaceFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int,
    tag: String
) {
    forceReplaceFragment(this.supportFragmentManager, fragment, isAddBack, container, tag)
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    showFragment(this.supportFragmentManager, fragment)
}

fun FragmentActivity.hideFragment(fragment: Fragment) {
    hideFragment(this.supportFragmentManager, fragment)
}

fun FragmentActivity.removeFragment(fragment: Fragment) {
    removeFragment(this.supportFragmentManager, fragment)
}

fun Fragment.addFragment(fragment: Fragment, isAddBack: Boolean, @IdRes container: Int) {
    addFragment(this.childFragmentManager, fragment, isAddBack, container)
}

fun Fragment.replaceFragment(fragment: Fragment, isAddBack: Boolean, @IdRes container: Int) {
    replaceFragment(this.childFragmentManager, fragment, isAddBack, container)
}

fun Fragment.forceAddFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int,
    tag: String
) {
    forceAddFragment(this.childFragmentManager, fragment, isAddBack, container, tag)
}

fun Fragment.forceReplaceFragment(
    fragment: Fragment,
    isAddBack: Boolean,
    @IdRes container: Int,
    tag: String
) {
    forceReplaceFragment(this.childFragmentManager, fragment, isAddBack, container, tag)
}

fun Fragment.showFragment(fragment: Fragment) {
    showFragment(this.childFragmentManager, fragment)
}

fun Fragment.hideFragment(fragment: Fragment) {
    hideFragment(this.childFragmentManager, fragment)
}

fun Fragment.removeFragment(fragment: Fragment) {
    removeFragment(this.childFragmentManager, fragment)
}

fun showDialogFragment(fm: FragmentManager, fragment: DialogFragment) {
    val tag = fragment.javaClass.simpleName
    val ft = fm.beginTransaction()
    val prev = fm.findFragmentByTag(tag)
    if (prev != null) {
        ft.remove(prev)
    }
    fragment.show(ft, tag)
}

fun showDialogFragmentAllowingStateLoss(fm: FragmentManager, fragment: DialogFragment) {
    val tag = fragment.javaClass.simpleName
    val ft = fm.beginTransaction()
    val prev = fm.findFragmentByTag(tag)
    if (prev != null) {
        ft.remove(prev)
    }
    Reflect.on(fragment).set("mDismissed", false)
    Reflect.on(fragment).set("mShownByMe", true)
    ft.add(fragment, tag)
    Reflect.on(fragment).set("mViewDestroyed", false)
    Reflect.on(fragment).set("mBackStackId", ft.commitAllowingStateLoss())
}

fun forceAddFragment(
    fm: FragmentManager, fragment: Fragment, isAddBack: Boolean, @IdRes container: Int, tag: String
) {
    val ft = fm.beginTransaction()
    if (fm.findFragmentByTag(tag) != null) {
        ft.show(fm.findFragmentByTag(tag)!!).commit()
        return
    }
    ft.add(container, fragment, tag)
    if (isAddBack) {
        ft.addToBackStack(tag)
    }
    ft.commit()
}

fun forceReplaceFragment(
    fm: FragmentManager, fragment: Fragment, isAddBack: Boolean, @IdRes container: Int, tag: String
) {
    val ft = fm.beginTransaction()
    if (fm.findFragmentByTag(tag) != null) {
        ft.show(fm.findFragmentByTag(tag)!!).commit()
        return
    }
    ft.replace(container, fragment, tag)
    if (isAddBack) {
        ft.addToBackStack(tag)
    }
    ft.commit()
}


fun addFragment(
    fm: FragmentManager, fragment: Fragment, isAddBack: Boolean, @IdRes container: Int
) {
    val tag = fragment.javaClass.simpleName
    val ft = fm.beginTransaction()
    if (fm.findFragmentByTag(tag) != null) {
        ft.show(fm.findFragmentByTag(tag)!!).commit()
        return
    }
    ft.add(container, fragment, tag)
    if (isAddBack) {
        ft.addToBackStack(tag)
    }
    ft.commit()
}

fun forceReplaceFragment(
    fm: FragmentManager, fragment: Fragment, isAddBack: Boolean, @IdRes container: Int
) {
    val tag = fragment.javaClass.simpleName
    val ft = fm.beginTransaction()
    ft.replace(container, fragment, tag)
    if (isAddBack) {
        ft.addToBackStack(tag)
    }
    ft.commit()
}

fun replaceFragment(
    fm: FragmentManager, fragment: Fragment, isAddBack: Boolean, @IdRes container: Int
) {
    val tag = fragment.javaClass.simpleName
    val ft = fm.beginTransaction()
    if (fm.findFragmentByTag(tag) != null) {
        ft.show(fm.findFragmentByTag(tag)!!).commit()
        return
    }
    ft.replace(container, fragment, tag)
    if (isAddBack) {
        ft.addToBackStack(tag)
    }
    ft.commit()
}

fun showFragment(fm: FragmentManager, fragment: Fragment) {
    if (!fragment.isHidden) {
        return
    }
    val ft = fm.beginTransaction()
    if (fragment.isAdded) {
        ft.show(fragment)
        ft.commit()
    }
}

fun hideFragment(fm: FragmentManager, fragment: Fragment) {
    if (fragment.isHidden) {
        return
    }
    val ft = fm.beginTransaction()
    if (fragment.isAdded) {
        ft.hide(fragment)
        ft.commit()
    }
}

fun removeFragment(fm: FragmentManager, fragment: Fragment) {
    val ft = fm.beginTransaction()
    if (fragment.isAdded) {
        ft.remove(fragment)
        ft.commit()
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> FragmentActivity.findFragmentByTag(tag: String): T {
    return this.supportFragmentManager.findFragmentByTag(tag) as T
}

@Suppress("UNCHECKED_CAST")
fun <T : Fragment> Fragment.findFragmentByTag(tag: String): T {
    return this.childFragmentManager.findFragmentByTag(tag) as T
}

@JvmOverloads
fun Activity.jumpTo(to: Class<*>, extras: Bundle? = null) {
    this.start(to, extras)
    this.finish()
}

/**
 * 选择相片
 */
fun Activity.startMatisseActivity() {
    Matisse.from(this)
        .choose(MimeType.ofImage(), true)
        .countable(false)
        .maxSelectable(1)
        .gridExpectedSize(this.dimensionPixelSize(R.dimen.dp_120))
        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
        .thumbnailScale(0.85f)
        .imageEngine(Glide4Engine())
        .capture(false)
        .theme(R.style.MyMatisse)
        .forResult(REQUEST_CODE_1029)
}

@JvmOverloads
fun Activity.start(to: Class<*>, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this, to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivity(intent)
}

@JvmOverloads
fun Fragment.start2(to: Class<*>, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this.requireActivity(), to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivity(intent)
}

@JvmOverloads
fun Activity.startForResult(to: Class<*>, requestCode: Int, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this, to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivityForResult(intent, requestCode)
}

@JvmOverloads
fun Fragment.startForResult2(to: Class<*>, requestCode: Int, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this.requireActivity(), to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivityForResult(intent, requestCode)
}

fun Activity.getExtras(): Bundle = this.intent.extras!!

@JvmOverloads
fun Activity.okFinish(intent: Intent? = null) {
    if (intent == null) {
        this.setResult(Activity.RESULT_OK)
    } else {
        this.setResult(Activity.RESULT_OK, intent)
    }
    this.finish()
}

fun EditText.showKeyboard() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.let {
        it.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Dialog.showKeyboardInDialog(target: EditText) {
    this.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    target.requestFocus()
}

fun Activity.hideKeyboard() = this.window.decorView.hideKeyboard()

fun View.hideKeyboard() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)?.let {
        it.hideSoftInputFromWindow(this.windowToken, 0)
    }
}