package com.example.pmf_engine_android.pmf_engine.base

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.*
import com.example.pmf_engine_android.R

@Suppress("UNCHECKED_CAST")
open class PMFBaseDialogFragment<B : ViewDataBinding> : DialogFragment() {
    protected lateinit var binding: B

    private var isTransparentBackground: Boolean = false
    private fun getBinding(): Class<B> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>
    }
    private fun setBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) {
        val method: Method?
        try {
            method = getBinding().getDeclaredMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
            val result = method.invoke(null, *arrayOf(inflater, container, false))
            binding = result as B
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
    }

    protected fun setDialogSize(newWidth: Double, newHeight: Double?) {
        if (dialog != null && dialog?.window != null) {
            val window = dialog?.window
            val size = Point()
            val display = window?.windowManager?.defaultDisplay
            display?.let {
                display.getSize(size)
                val width = size.x
                val height = size.y

                window.setLayout(
                    ((width * newWidth).toInt()),
                    if (newHeight != null) {
                        ((height * newHeight).toInt())
                    } else {
                        (ViewGroup.LayoutParams.WRAP_CONTENT)
                    }
                )
                if (isTransparentBackground) {
                    window.attributes?.also { attributes ->
                        attributes.dimAmount = 0.0f
                        window.attributes = attributes
                    }
                }
            }
            dialog?.window?.setGravity(Gravity.CENTER)
        }
    }
}