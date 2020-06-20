package com.anand.mvvmskeletonarchitecture.common.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.RawRes
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.anand.mvvmskeletonarchitecture.R
import com.anand.mvvmskeletonarchitecture.common.util.HelperUtil.gson
import com.anand.mvvmskeletonarchitecture.ui.common.fragments.BaseFragment
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import kotlin.math.roundToLong

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable) {
    Picasso.get().load(url).placeholder(progressDrawable)
        .error(R.drawable.ic_download_error).into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

fun NavController.navigateWithClearStack(
    @IdRes destId: Int,
    args: Bundle? = null,
    anim: Boolean = false
) {
    val builder = NavOptions.Builder()
        .setPopUpTo(R.id.navGraph, true)
        .setLaunchSingleTop(true)
    if (anim) {
        try {
            builder.setEnterAnim(R.anim.slide_anim_in)
                .setExitAnim(R.anim.slide_anim_out)
                .setPopEnterAnim(R.anim.slide_pop_anim_in)
                .setPopExitAnim(R.anim.slide_pop_anim_out)
        } catch (e: IllegalArgumentException) {
            Log.e("NavController", "Multiple navigation attempts handled ${e.message}")
        }
    }
    this.navigate(
        destId,
        args,
        builder.build()
    )
}

fun NavController.navigateWithAnim(directions: NavDirections) {
    try {
        this.navigate(
            directions.actionId,
            directions.arguments,
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_anim_in)
                .setExitAnim(R.anim.slide_anim_out)
                .setPopEnterAnim(R.anim.slide_pop_anim_in)
                .setPopExitAnim(R.anim.slide_pop_anim_out).build()
        )
    } catch (e: IllegalArgumentException) {
        Log.e("NavController", "Multiple navigation attempts handled ${e.message}")
    }
}

fun NavController.navigateWithAnim(@IdRes destId: Int, args: Bundle? = null) {
    try {
        this.navigate(
            destId,
            args,
            NavOptions.Builder()
                .setEnterAnim(R.anim.slide_anim_in)
                .setExitAnim(R.anim.slide_anim_out)
                .setPopEnterAnim(R.anim.slide_pop_anim_in)
                .setPopExitAnim(R.anim.slide_pop_anim_out).build()
        )
    } catch (e: IllegalArgumentException) {
        Log.e("NavController", "Multiple navigation attempts handled ${e.message}")
    }
}

/**
 * Navigate with animation and disable touch to stop unnecessary clicks
 * before transition completed.
 */
fun Fragment.navigateWithAnimAndDisableTouch(@IdRes destId: Int, args: Bundle? = null) {
    activity?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
    this.findNavController().navigateWithAnim(destId, args)
}

fun Fragment.replaceFragment(fragment: BaseFragment) {
    if (childFragmentManager.findFragmentByTag(fragment.TAG) == null) {
        val fragmentTransaction = childFragmentManager.beginTransaction()
        childFragmentManager.findFragmentByTag(fragment.TAG)?.let {
            fragmentTransaction.remove(it)
        }
        fragmentTransaction.replace(R.id.fragmentFrame, fragment, fragment.TAG)
        fragmentTransaction.commitAllowingStateLoss()
        childFragmentManager.executePendingTransactions()
    }
}

// shared preference extension functions
inline fun <reified T> SharedPreferences.set(key: String, value: T) {
    val editor = edit()
    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> throw IllegalArgumentException("This type can't be stored in shared preferences")
    }
    editor.apply()
}

fun SharedPreferences.addWithSeparator(key: String, value: String) {
    var prefValues = getString(key, "")
    if (!prefValues.isNullOrEmpty()) {
        prefValues = "$prefValues,$value"
    } else {
        prefValues = value
    }
    val editor = edit()
    editor.putString(key, prefValues)
    editor.apply()
}

fun SharedPreferences.commit() = edit().commit()

fun SharedPreferences.remove(key: String) {
    edit().remove(key).apply()
}

fun SharedPreferences.clear() {
    edit().clear().apply()
}

fun SharedPreferences.getDecryptedString(
    key: String,
    def: String?,
    encryptionUtil: EncryptionUtil
): String {
    return encryptionUtil.decrypt(this.getString(key, def) ?: "")
}

fun SharedPreferences.setEncryptedString(
    key: String,
    value: String,
    encryptionUtil: EncryptionUtil
) {
    this.set(key, encryptionUtil.encrypt(value))
}

fun Context.isAccessibilityModeOn(): Boolean {
    val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    return accessibilityManager.isEnabled && accessibilityManager.isTouchExplorationEnabled
}


inline fun <reified T> Context.loadJsonData(@RawRes jsonFileId: Int): T {
    val inputStream = this.resources.openRawResource(jsonFileId)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val json = String(buffer)
    return gson.fromJson(json, object : TypeToken<T>() {}.type)
}

fun Context.isLocationEnabled(): Boolean {
    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gpsEnabled = false

    try {
        gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {
        Log.e("Context", ex.toString())
    }
    return gpsEnabled
}

fun Fragment.open(
    @IdRes containerToReplace: Int,
    fm: FragmentManager,
    showDefaultAnimation: Boolean = true
) {
    val transaction = fm.beginTransaction()
    if (showDefaultAnimation) {
        transaction.setCustomAnimations(
            R.anim.slide_anim_in,
            R.anim.slide_anim_out,
            R.anim.slide_pop_anim_in,
            R.anim.slide_pop_anim_out
        )
    }
    transaction.replace(containerToReplace, this)
        .addToBackStack(null)
        .commit()
    fm.executePendingTransactions()
}

fun FragmentManager.popFragment() {
    this.popBackStack()
    this.executePendingTransactions()
}

fun String.trimPeriods(): String {
    return this.replace("\\.+$".toRegex(), "")
}

// https://stackoverflow.com/questions/35513636/multiple-variable-let-in-kotlin
// Solution to multiple variable check in let
inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}

// Solution to check all elements are null
inline fun <T : Any> guardLet(vararg elements: T?, closure: () -> Unit) {
    if (elements.all { it == null }) {
        closure()
    }
}

fun RemoteViews.setTextColor(viewIds: IntArray, @ColorInt color: Int) {
    viewIds.forEach { setTextColor(it, color) }
}

fun RemoteViews.setVisibility(viewIds: IntArray, visibility: Int) {
    viewIds.forEach { setViewVisibility(it, visibility) }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToLong() / multiplier
}

fun Context.getDrawableByName(name: String): Drawable? =
    getDrawable(resources.getIdentifier(name, "drawable", packageName))

fun Context.isScreenReaderOn(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (am.isEnabled) {
        val serviceInfoList =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        if (!serviceInfoList.isEmpty())
            return true
    }
    return false
}

/**
 * Adds bullet mark before text.
 */
infix fun TextView.displayAsbullet(value: String) {
    val span = SpannableString(value)
    span.setSpan(BulletSpan(15), 0, value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = span
}