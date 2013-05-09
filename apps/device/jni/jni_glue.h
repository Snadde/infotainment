/*
 Copyright (c) 2012, Spotify AB
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * Neither the name of Spotify AB nor the names of its contributors may
 be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL SPOTIFY AB BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#include <jni.h>

jclass find_class_from_native_thread(JNIEnv **envSetter);
void call_static_void_method(const char *method_name);

extern "C" {
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_init(JNIEnv *je, jclass jc, jobject loader, jstring storage_path);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_destroy(JNIEnv *je, jclass jc);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_login(JNIEnv *je, jclass jc, jstring username, jstring password);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_toggleplay(JNIEnv *je, jclass jc, jstring uri);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_playnext(JNIEnv *je, jclass jc, jstring j_uri);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_seek(JNIEnv *je, jclass jc, jfloat position);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_star(JNIEnv *je, jclass jc);
JNIEXPORT void JNICALL Java_se_chalmers_pd_device_LibSpotifyWrapper_unstar(JNIEnv *je, jclass jc);

}
