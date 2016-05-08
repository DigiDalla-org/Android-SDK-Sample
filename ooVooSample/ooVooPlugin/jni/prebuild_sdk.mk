LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE           := oovoosdk
LOCAL_SHARED_LIBRARIES := ../../libs/armeabi-v7a/libooVooSdk.so
include $(PREBUILT_STATIC_LIBRARY)
