package com.olegdev.documentcreator.extensions

import androidx.lifecycle.MutableLiveData

fun <T: Any?> MutableLiveData<T>.default(initialValue: T) = apply { value = initialValue }
fun <T: Any?> MutableLiveData<T>.set(newValue: T) = apply { postValue(newValue) }