package lei.cheng.performancetools.init

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipFile

/**
 * @author chenglei01
 * @date 2024/5/2
 * @time 00:43
 */


@RequiresApi(Build.VERSION_CODES.O)
fun copyApk(context: Context, soname:String):File?{

    val path = File(context.cacheDir, "lib${soname}.so")
    val copiedApkDir = File(context.cacheDir, "mineapk")
    if (path.exists()) {
        path.delete()
    }
    if (!path.exists()) {
        val findLibrary =
            ClassLoader::class.java.getDeclaredMethod("findLibrary", String::class.java)
        val jvmtiSoLibPath = findLibrary.invoke(context.classLoader, soname) as? String?:return null
        val zipSplite = jvmtiSoLibPath.split("!")
        if (zipSplite.size != 2)  return null
        val apkFile = zipSplite[0].replace("!", "") // apk路径
        val soPath = zipSplite[1]
//        Log.e("chenglei", "apkFile:$apkFile, soPath:$soPath")
//        Log.e("chenglei", "libjvmtidemo.so's path is $jvmtiSoLibPath, copied path:${path}")

        copiedApkDir.mkdirs()

        val copiedApkPath = File(copiedApkDir, "mine.apk")
        if (copiedApkPath.exists()) {
            copiedApkPath.delete()
        }
        Files.copy(Paths.get(apkFile), Paths.get(copiedApkPath.absolutePath))

//        Log.e("chenglei", "copiedApkPath:$copiedApkPath")

        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(copiedApkPath.absolutePath, 0)
        val applicationInfo = packageInfo?.applicationInfo
        applicationInfo?.sourceDir = copiedApkPath.absolutePath

        applicationInfo?:return null
        ZipFile(applicationInfo.sourceDir)
            .use {zip->
                val entry = zip.getEntry(soPath.replaceFirst(File.separator,""))
                val inputStream = zip.getInputStream(entry)
                FileOutputStream(path)
                    .use { output->
                        inputStream.copyTo(output)
                    }
                zip.close()
            }
        Log.e("chenglei", "so copy成功:${path.absolutePath}")
    } else {
        Log.e("chenglei", "${path}已经存在，跳过")
    }
    return path

}