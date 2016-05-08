#include <jni.h>
#include <android/log.h>
#include "effect/EffectSampleFactory.h"
static oovoo::sdk::plugin_factory::ptr factory ;
static jclass java_level_class ;

static jlong CreatePlugin(JNIEnv* env, jobject thiz)
{
	if(!factory){
		factory = EffectSample::EffectSampleFactory::createPluginFactory();
	}
	return (jlong)factory.get() ;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* pvt)
{
	JNIEnv* env ;

	if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK)
	{
		return -1;
	}
	java_level_class = env->FindClass("com/oovoo/sdk/plugin/ooVooPluginFactory");
	static JNINativeMethod methods[] =
	{
			{"createPlugin"    , "()J", (jlong*)CreatePlugin }
	};

	if (env->RegisterNatives(java_level_class, methods, sizeof(methods) / sizeof(JNINativeMethod)) < 0)
	{
		return -2 ;
	}
	return JNI_VERSION_1_6;
}


JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* pvt)
{

}
