# function to find all *.cpp files under a directory
define all-cpp-files-under
$(patsubst ./%,%, \
$(shell cd $(LOCAL_PATH) ; \
find $(1) -type f -name "*.cpp" ) \
)
endef


define all-h-files-under
$(patsubst ./%,%, \
$(shell cd $(LOCAL_PATH) ; \
find $(1) -type f -name "*.h" ) \
)
endef


LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OOVOO_SDK_PATH := oovoo_sdk/armeabi-v7a

LOCAL_CPPFLAGS += $(DEBUG_FLAGS) -std=c++1y -fno-strict-aliasing -fpermissive
LOCAL_CPPFLAGS += -DANDROID -D__ANDROID__

LOCAL_SHARE_LIBRARIES    := -L$(OOVOO_SDK_PATH) -looVooSdk -lva -lva-android
LOCAL_MODULE             := ooVooPlugin
LOCAL_SRC_FILES          := ooVooPlugin.cpp
LOCAL_SRC_FILES          += ./effect/EffectPlugin.cpp ./effect/EffectSampleFactory.cpp
LOCAL_C_INCLUDES         :=  ./effect
LOCAL_C_INCLUDES         +=  ./pluginapi

$(info LOCAL_C_INCLUDES = $(LOCAL_C_INCLUDES))

include $(BUILD_SHARED_LIBRARY)
