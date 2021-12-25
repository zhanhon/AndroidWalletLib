package com.ramble.ramblewallet.model

import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlin.reflect.KProperty


/**
 * 时间　: 2021/12/25 15:47
 * 作者　: potato
 * 描述　:
 */
class ContentView<in T : android.app.Activity, out R : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) {
    private var value: R? = null
    operator fun getValue(ref: T, property: KProperty<*>): R {
        value = value ?: DataBindingUtil.setContentView(ref, layoutRes)
        return value!!
    }
}

class ActivityViewModel<in T : Activity, out R : ViewModel>(private val clazz: Class<R>) {
    private var value: R? = null
    operator fun getValue(thisRef: T, property: KProperty<*>): R {
        value = value ?: ViewModelProviders.of(thisRef, thisRef.viewModelFactory).get<R>(clazz)
        return value!!
    }
}

//class FragmentViewModel<in T : Fragment, out R : ViewModel>(private val clazz: Class<R>) {
//    private var value: R? = null
//    operator fun getValue(thisRef: T, property: KProperty<*>): R {
//        if (value == null) {
//            value = when (thisRef.viewModelSource()) {
//                ViewModelSource.ACTIVITY -> {
//                    ViewModelProviders.of(thisRef.activity!!, thisRef.viewModelFactory).get(clazz)
//                }
//                ViewModelSource.PARENT_FRAGMENT -> {
//                    ViewModelProviders.of(thisRef.parentFragment!!, thisRef.viewModelFactory).get(clazz)
//                }
//                ViewModelSource.NONE -> {
//                    ViewModelProviders.of(thisRef, thisRef.viewModelFactory).get(clazz)
//                }
//            }
//        }
//        return value!!
//    }
//}

//class DialogFragmentViewModel<in T : AppCompatDialogFragment, out R : ViewModel>(
//    private val clazz: Class<R>
//) {
//    private var value: R? = null
//    operator fun getValue(thisRef: T, property: KProperty<*>): R {
//        if (value == null) {
//            value = when (thisRef.viewModelSource()) {
//                ViewModelSource.ACTIVITY -> {
//                    ViewModelProviders.of(thisRef.activity!!, thisRef.viewModelFactory).get(clazz)
//                }
//                ViewModelSource.PARENT_FRAGMENT -> {
//                    ViewModelProviders.of(thisRef.parentFragment!!, thisRef.viewModelFactory).get(clazz)
//                }
//                ViewModelSource.NONE -> {
//                    ViewModelProviders.of(thisRef, thisRef.viewModelFactory).get(clazz)
//                }
//            }
//        }
//        return value!!
//    }
//}