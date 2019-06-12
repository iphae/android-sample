#Liang.tang mask for mtk camera in eng build for camera tuning 2019/3/23
ifneq ($(TARGET_BUILD_VARIANT) , user)
ifeq ($(MTK_CAMERA_APP_VERSION), 3)
LOCAL_ROOT_PATH:= $(call my-dir)

include $(LOCAL_ROOT_PATH)/host/Android.mk
include $(LOCAL_ROOT_PATH)/portability/Android.mk
include $(LOCAL_ROOT_PATH)/tests/Android.mk
include $(LOCAL_ROOT_PATH)/feature/setting/matrixdisplay/matrixdisplay_ext/Android.mk
include $(LOCAL_ROOT_PATH)/testscat/Android.mk
#include $(LOCAL_ROOT_PATH)/feature/pluginroot/Android.mk
#include $(LOCAL_ROOT_PATH)/feature/mode/panorama/Android.mk
#include $(LOCAL_ROOT_PATH)/feature/mode/pip/Android.mk
#include $(LOCAL_ROOT_PATH)/feature/mode/vsdof/Android.mk
#include $(LOCAL_ROOT_PATH)/feature/mode/slowmotion/Android.mk
endif
endif
#Liang.tang mask for mtk camera in eng build for camera tuning 2019/3/23
