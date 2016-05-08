//
// object_ptr.h
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
#ifndef _OBJECTS_PTR_H_
#define _OBJECTS_PTR_H_

#include <ostream>
#include <assert.h>

//
//  object_ptr
//
//  A smart pointer that uses intrusive reference counting.
//
//  Relies on unqualified calls to
//
//      void object_ptr_add_ref(T * p);
//      void object_ptr_release(T * p);
//
//          (p != 0)
//
//  The object is responsible for destroying itself.
//
//
#include <atomic>
#include <utility>
#include <iostream>

namespace oovoo {
namespace sdk {

template <class T>
class object_ptr
{
public:

	typedef T element_type;

	object_ptr()
		: px(0)
	{
	}

	object_ptr(T* p, bool add_ref = true)
		: px(p)
	{
		if (px && add_ref)
		{
			object_ptr_add_ref(px);
		}
	}

	object_ptr(const object_ptr& source)
		: px(source.px)
	{
		if (px)
		{
			object_ptr_add_ref(px);
		}
	}

	template <class U>
	object_ptr(const object_ptr<U>& source)
		: px(source.get())
	{
		if (px)
		{
			object_ptr_add_ref(px);
		}
	}

	object_ptr(object_ptr&& source)
		: px(source.px)
	{
		source.px = 0;
	}

	~object_ptr()
	{
		if (px)
		{
			object_ptr_release(px);
		}
	}

	object_ptr& operator = (const object_ptr& source)
	{
		object_ptr(source).swap(*this);
		return *this;
	}

	template <class U>
	object_ptr& operator = (const object_ptr<U>& source)
	{
		object_ptr(source).swap(*this);
		return *this;
	}

	object_ptr& operator = (object_ptr && source)
	{
		object_ptr(std::move(source)).swap(*this);
		return *this;
	}

	template <class U>
	object_ptr& operator = (object_ptr<U> && source)
	{
		object_ptr(std::move(source)).swap(*this);
		return *this;
	}

	object_ptr& operator = (T* source)
	{
		object_ptr(source).swap(*this);
		return *this;
	}

	void reset()
	{
		object_ptr().swap(*this);
	}

	void reset(T* source)
	{
		object_ptr(source).swap(*this);
	}

	T* get() const
	{
		return px;
	}

	T& operator * () const
	{
		assert(px);
		return *px;
	}

	T* operator -> () const
	{
		assert(px);
		return px;
	}

	explicit operator bool() const
	{
		return (px != nullptr);
	}

	void swap(object_ptr& source)
	{
		using std::swap;
		swap(px, source.px);
	}

private:

	T* px;

};

template<class T, class U>
inline bool operator == (const object_ptr<T>& a, const object_ptr<U>& b)
{
	return a.get() == b.get();
}

template<class T, class U>
inline bool operator != (const object_ptr<T>& a, const object_ptr<U>& b)
{
	return a.get() != b.get();
}

template<class T, class U>
inline bool operator == (const object_ptr<T>& a, U* b)
{
	return a.get() == b;
}

template<class T, class U>
inline bool operator != (const object_ptr<T>& a, U* b)
{
	return a.get() != b;
}

template<class T, class U>
inline bool operator == (T* a, const object_ptr<U>& b)
{
	return a == b.get();
}

template<class T, class U>
inline bool operator != (T* a, const object_ptr<U>& b)
{
	return a != b.get();
}

// nullptr support for WinRT 8.1
template<class T>
inline bool operator==(object_ptr<T> const& p, std::nullptr_t)
{
	return p.get() == 0;
}

template<class T>
inline bool operator==(std::nullptr_t, object_ptr<T> const& p)
{
	return p.get() == 0;
}

template<class T>
inline bool operator!=(object_ptr<T> const& p, std::nullptr_t)
{
	return p.get() != 0;
}

template<class T>
inline bool operator!=(std::nullptr_t, object_ptr<T> const& p)
{
	return p.get() != 0;
}

template<class T>
inline bool operator < (const object_ptr<T>& a, const object_ptr<T>& b)
{
	return std::less<T*>()(a.get(), b.get());
}

template<class T>
inline void swap(object_ptr<T>& a, object_ptr<T>& b)
{
	a.swap(b);
}

template<class T>
inline T* get_pointer(const object_ptr<T>& p)
{
	return p.get();
}

template<class T, class U>
inline object_ptr<T> static_pointer_cast(const object_ptr<U>& p)
{
	return static_cast<T*>(p.get());
}

template<class T, class U>
inline object_ptr<T> const_pointer_cast(const object_ptr<U>& p)
{
	return const_cast<T*>(p.get());
}

template<class T, class U>
inline object_ptr<T> dynamic_pointer_cast(const object_ptr<U>& p)
{
	return dynamic_cast<T*>(p.get());
}

template<class E, class T, class Y>
inline std::basic_ostream<E, T>& operator << (std::basic_ostream<E, T>& os, const object_ptr<Y>& p)
{
	os << p.get();
	return os;
}

template <class T>
struct hash;

template <class T>
inline std::size_t hash_value(object_ptr<T> const& p)
{
	return std::hash<T*>()(p.get());
}



template<class Base>
struct ObjImpl : public Base
{
	template<class ..._Args>
	ObjImpl(_Args&& ...__args) : Base(std::forward<_Args>(__args)...), c(0) {}
	virtual ~ObjImpl() {}

private:
	virtual unsigned long add_ref() override
	{
		return ++c;
	}

	virtual unsigned long release() override
	{
		unsigned long l = --c;

		if (l == 0)
		{
			delete this;
		}

		return l;
	}

	virtual unsigned long use_count() override
	{
		return c;
	}

	std::atomic<unsigned long> c;
};

template<class T, class ..._Args>
object_ptr<T> make_object(_Args&& ...__args)
{
	return new ObjImpl<T>(std::forward<_Args>(__args)...);
}

template <typename T, typename U>
object_ptr<T> dyn_cast(object_ptr<U> p)
{
	if (p)
	{
		return (T*)(p->get_object()->get_interface(T::get_clsid()));
	}

	return nullptr;
}

template <typename T, typename U, typename TypeCheck = typename std::enable_if<std::is_base_of<T, U>::value>::type>
object_ptr<T> base_cast(object_ptr<U> p)
{
	return dyn_cast<T>(p);
}

template< typename T>
void object_ptr_add_ref(T* p)
{
	p->get_object()->add_ref();
}

template< typename T>
void object_ptr_release(T* p)
{
	p->get_object()->release();
}

} // sdk
} // namespace oovoo

#endif // _OBJECTS_PTR_H_
