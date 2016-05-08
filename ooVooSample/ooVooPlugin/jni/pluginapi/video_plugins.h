//
// video_plugins.h
//
// Created by ooVoo on May 17, 2015
//
// © 2015 ooVoo, LLC.  Used under license.
//
// This product includes software from the following open source projects:
//
// Google WebM project:
// http://www.webmproject.org/license/bitstream/
// http://www.webmproject.org/license/software/
// http://www.webmproject.org/license/additional/
// Copyright (c) 2010, Google Inc. All rights reserved.
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
// •         Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// •         Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
// •         Neither the name of Google nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// OpenSSL project:
// http://www.openssl.org/source/license.html
// Copyright (c) 1998-2011 The OpenSSL Project.  All rights reserved.
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software must display the following acknowledgment: "This product includes software developed by the OpenSSL Project for use in the OpenSSL Toolkit. (http://www.openssl.org/)"
// 4. The names "OpenSSL Toolkit" and "OpenSSL Project" must not be used to endorse or promote products derived from this software without prior written permission. For written permission, please contact openssl-core@openssl.org.
// 5. Products derived from this software may not be called "OpenSSL" nor may "OpenSSL" appear in their names without prior written permission of the OpenSSL Project.
// 6. Redistributions of any form whatsoever must retain the following acknowledgment: "This product includes software developed by the OpenSSL Project for use in the OpenSSL Toolkit (http://www.openssl.org/)"
// THIS SOFTWARE IS PROVIDED BY THE OpenSSL PROJECT ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OpenSSL PROJECT OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// This license also includes a requirement to comply with the “Original SSLeay License,” which includes the following terms:
// This library is free for commercial and non-commercial use as long as the following conditions are adhered to.  The following conditions apply to all code found in this distribution, be it the RC4, RSA, lhash, DES, etc., code; not just the SSL code.  The SSL documentation included with this distribution is covered by the same copyright terms except that the holder is Tim Hudson (tjh@cryptsoft.com).
// Copyright remains Eric Young's, and as such any Copyright notices in the code are not to be removed.  If this package is used in a product, Eric Young should be given attribution as the author of the parts of the library used.  This can be in the form of a textual message at program startup or in documentation (online or textual) provided with the package.
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the copyright notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software must display the following acknowledgement:  "This product includes cryptographic software written by Eric Young (eay@cryptsoft.com)" The word 'cryptographic' can be left out if the routines from the library being used are not cryptographic related :-).
// 4. If you include any Windows specific code (or a derivative thereof) from the apps directory (application code) you must include an acknowledgement: "This product includes software written by Tim Hudson (tjh@cryptsoft.com)"
// THIS SOFTWARE IS PROVIDED BY ERIC YOUNG ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// The license and distribution terms for any publically available version or derivative of this code cannot be changed.  i.e. this code cannot simply be copied and put under another distribution license [including the GNU Public License.]

// boost:
// http://www.boost.org/users/license.html
// Boost Software License – Version 1.0 – August 17th, 2003
// Permission is hereby granted, free of charge, to any person or organization obtaining a copy of the software and accompanying documentation covered by this license (the ‘Software”) to use, reproduce, display, distribute, execute, and transmit the Software, and to prepare derivative works of the Software, and to permit third-parties to whom the Software is furnished to do so, all subject to the following:
// The copyright notices in the Software and this entire statement, including the above license grant, this restriction and the following disclaimer, must be included in all copies of the Software, in whole or in part, and all derivative works of the software, unless such copies or derivative works are solely in the form of machine-executable object code generated by a source language processor.
// The software is provided “as is”, without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose, title and non-infringement. In no event shall the copyright holders or anyone distributing the software be liable for any damages or other liability, whether in contract, tort or otherwise arising from, out of or in connection with the software or the use or other dealings in the software.

// LibYuv project:
// https://github.com/lemenkov/libyuv/blob/master/LICENSE
// Copyright 2011 The LibYuv Project Authors. All rights reserved.
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
// o   Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
// o   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
// o   Neither the name of Google nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// UTF8 convert:
// Permission is hereby granted, free of charge, to any person or organization obtaining a copy of the software and accompanying documentation covered by this license (the “Software”) to use, reproduce, display, distribute, execute, and transmit the Software, and to prepare derivative works of the Software, and to permit third-parties to whom the Software is furnished to do so, all subject to the following:
// The copyright notices in the Software and this entire statement, including the above license grant, this restriction and the following disclaimer, must be included in all copies of the Software, in whole or in part, and all derivative works of the Software, unless such copies or derivative works are solely in the form of machine-executable object code generated by a source language processor.
// THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR ANYONE DISTRIBUTING THE SOFTWARE BE LIABLE FOR ANY DAMAGES OR OTHER LIABILITY, WHETHER IN CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.”
//
//
#ifndef _VIDEO_PLUGINS_H_
#define _VIDEO_PLUGINS_H_

#include <chrono>
#include "media_plugins.h"
#include "video.h"

namespace oovoo {
namespace sdk {

// ----------------- video_data
class video_data : public media_data
{
	OOVOO_CLASS(video_data, media_data, "{3f7d5332-91c8-4c0b-af00-9ed73d88ac38}");

public:
	virtual uint16_t width() const = 0;
	virtual uint16_t height() const = 0;
	virtual color_format format() const = 0;

	// plane_info, strides, etc...
	virtual int num_planes() const = 0;
	virtual void* plane_ptr(const int num) const = 0;
	virtual int plane_pitch(const int num) const = 0;
};

// ---------------- video_frame

class video_frame : public IObject
{
	OOVOO_CLASS(video_frame, IObject, "{455de2f9-3e3b-4a44-8eaf-cb62f17d216e}");

public:
	virtual video_data::ptr get_video_data() = 0;

	virtual video_type type() const = 0;
	virtual color_format format() const = 0;

	virtual unsigned int texture() const = 0;

	virtual uint16_t width() const = 0;
	virtual uint16_t height() const = 0;

	virtual uint16_t frame_number() const = 0;
	virtual uint64_t time() const = 0;

	virtual bool key_frame() const = 0;

	virtual bool mirror() const = 0;

	virtual int rotation_angle() const = 0;
	virtual int device_rotation_angle() const = 0;

	virtual std::chrono::steady_clock::time_point capture_time() const = 0;
	virtual std::chrono::steady_clock::time_point net_in_time() const = 0;
};

// ---------------- video_effect
class video_effect : public plugin
{
	OOVOO_CLASS(video_effect, plugin, "{c2ea6cc4-6a2e-4b0c-a3bf-187f74f4602a}");

public:
	virtual video_frame::ptr process(video_frame::ptr frame) = 0; // does the business logic
	virtual void destroyResources() = 0;
};

// Additional information about camera.
// Can be received throw dyn_cast<video_effect_info> from plugin_info::ptr or video_effect::ptr
class video_effect_info : public plugin_info
{
	OOVOO_CLASS(video_effect_info, plugin_info, "{9d66e1d5-ce1c-4a0f-98d8-8ae8c81463df}");
public:
	virtual const char* url() const { return ""; }
};

// ----------------video_capture_info

// Additional information about camera. Optional.
// Can be received throw dyn_cast<video_capture_info> from plugin_info::ptr or IVideoCapture::ptr
class video_capture_info : public plugin_info
{
	OOVOO_CLASS(video_capture_info, plugin_info, "{7cc959cd-fb48-44d2-bd6f-af2e1a944be4}");

public:
	enum camera_position
	{
		back,
		front,
		unknown
	};

	struct resolution_info
	{
		int  width;
		int  height;
		int  max_fps;
		int  prefered_fps;
		bool prefered;

		resolution_info(int w = 0, int h = 0, int pref_fps = 15, int fps = 15, bool pref = false)
			: width(w), height(h), max_fps(fps), prefered_fps(pref_fps), prefered(pref) {}
	};

	virtual camera_position camera_type() const { return unknown; }

	virtual resolution_info* supported_resolutions(int& num_resolutions) = 0;
};

class video_capture_listener : public IObject
{
	OOVOO_CLASS(video_capture_listener, IObject, "{9be0f7e6-a0bd-4365-ba3c-39ccbc5336d6}");

public:
	virtual void on_preview_frame(video_frame::ptr frame) = 0;
	virtual void on_output_frame(video_frame::ptr frame) = 0;
};

class video_capture : public media_device
{
	OOVOO_CLASS(video_capture, media_device, "{0eede23f-7ad9-4e67-a8f9-ac687b527bac}");

public:
	virtual void set_listener(video_capture_listener::ptr handler) = 0;
	virtual sdk_error set_output_format(const video_type& type, const color_format& format) = 0;
	virtual sdk_error set_preview_format(const color_format& format) = 0;

	virtual sdk_error start(const int width, const int height, const int fps) = 0;
	virtual sdk_error stop() = 0;

	virtual sdk_error effect(video_effect::ptr effect) = 0;
	virtual video_effect::ptr effect() const = 0;
    virtual video_capture_info::ptr info() const= 0;
	virtual video_capture_info::resolution_info actual_resolution_info() const = 0;
};

// ---------------- video_render
class video_render : public IObject
{
	OOVOO_CLASS(video_render, IObject, "{e7a7ded8-bb67-4c11-a50f-b9d17a67612a}");

public:
	virtual void render(const video_frame::ptr frame) = 0;

	virtual sdk_error start() = 0;
	virtual sdk_error stop() = 0;
};

} // sdk
} // namespace oovoo

#endif // !_VIDEO_PLUGINS_H_
