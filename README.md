# Api [![](https://www.jitpack.io/v/FengChenSunshine/BaseModule.svg)](https://www.jitpack.io/#FengChenSunshine/BaseModule)

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
    }

### Step 2. Add the dependency
    dependencies {
	    implementation 'com.github.FengChenSunshine:BaseModule:1.0.0'
	}

## 历史版本
### 1.0.1
1.移除ApiCallback类泛型必须继承com.duanlu.model.Model限制，因为这个限制会导致List<T>等格式无法使用.
2.修改网络请求接口相关包名为api.
3.升级Utils工具类库版本到1.0.2.
4.增加ModuleApplication类里getAuthority()方法默认实现为【getPackageName() + ".FileProvider"】.

### 1.0.0
1.发布1.0.0


