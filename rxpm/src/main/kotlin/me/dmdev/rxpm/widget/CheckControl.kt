package me.dmdev.rxpm.widget

import android.widget.CompoundButton
import com.jakewharton.rxbinding2.view.enabled
import com.jakewharton.rxbinding2.widget.checkedChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.dmdev.rxpm.PresentationModel.Action
import me.dmdev.rxpm.PresentationModel.State

/**
 * @author Dmitriy Gorbunov
 */
class CheckControl(initialChecked: Boolean = false,
                   initialEnabled: Boolean = true) {

    val checked = State(initialChecked)
    val enabled = State(initialEnabled)
    val checkedChanges = Action<Boolean>()

    init {
        checkedChanges.relay
                .filter { it != checked.value }
                .subscribe(checked.relay)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun CompoundButton.bind(checkControl: CheckControl): Disposable {
    return CompositeDisposable().apply {
        var editing = false
        addAll(
                checkControl.checked.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            editing = true
                            isChecked = it
                            editing = false
                        },

                checkedChanges()
                        .skipInitialValue()
                        .filter { !editing }
                        .subscribe(checkControl.checkedChanges.consumer),

                checkControl.enabled.observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(enabled())
        )
    }
}