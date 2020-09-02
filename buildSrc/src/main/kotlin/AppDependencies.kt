const val kotlinAndroid: String = "android"
const val kotlinAndroidExtension: String = "android.extensions"
const val kotlinKapt: String = "kapt"

object Config {
    object Version {
        const val minSdkVersion: Int = 19
        const val compileSdkVersion: Int = 29
        const val targetSdkVersion: Int = 29
        const val versionName: String = "1.0"
        const val versionCode: Int = 1
    }

    const val isMultiDexEnabled: Boolean = true
    const val isDatabindingEnabld: Boolean = true

    object Android {
        const val applicationId: String = "com.zizohanto.android.tobuyList"
        const val testInstrumentationRunner: String = "androidx.test.runner.AndroidJUnitRunner"
    }
}

interface Libraries {
    val components: List<String>
}

object Dependencies {
    object AndroidX : Libraries {
        object Version {
            const val appCompat: String = "1.3.0-alpha02"
            const val constraintLayout: String = "constraintlayout"
            const val recyclerView: String = "1.2.0-alpha05"
            const val swipeRefreshLayout: String = "1.2.0-alpha01"
        }

        private const val appCompat: String = "androidx.appcompat:appcompat:${Version.appCompat}"
        private const val constraintLayout: String = "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
        private const val recyclerView: String = "androidx.recyclerview:recyclerview:${Version.recyclerView}"
        private const val swiperefreshLayout: String = "androidx.swiperefreshlayout:swiperefreshlayout:${Version.swipeRefreshLayout}"

        override val components: List<String>
            get() = listOf(appCompat, constraintLayout, recyclerView, swiperefreshLayout)
    }

    object View : Libraries {
        object Version {
            const val materialComponent: String = "1.3.0-alpha02"
        }

        private const val materialComponent: String = "com.google.android.material:material:${Version.materialComponent}"
        override val components: List<String> = listOf(materialComponent)
    }

    object Others : Libraries {
        object Version {
            const val guava: String = "29.0-android"
            const val gson: String = "2.8.6"
        }

        private const val guava: String = "com.google.guava:guava:${Version.guava}"
        private const val gson: String = "com.google.code.gson:gson:${Version.gson}"

        override val components: List<String> = listOf(guava, gson)
    }

    object Cache {
        object Version {
            const val room: String = "2.3.0-alpha02"
        }

        object AnnotationProcessor {
            const val room: String = "androidx.room:room-compiler:${Version.room}"
        }

        const val roomRuntime: String = "androidx.room:room-runtime:${Version.room}"
    }

    object Test {
        object Version {
            const val junit: String = "4.13"
            const val runner: String = "1.3.0"
            const val espresso: String = "3.3.0"
        }

        const val junit: String = "junit:junit:${Version.junit}"
        const val runner: String = "androidx.test:runner:${Version.runner}"
        const val espresso: String = "androidx.test.espresso:espresso-core:${Version.espresso}"
    }
}
