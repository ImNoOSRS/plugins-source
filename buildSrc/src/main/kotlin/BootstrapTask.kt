import com.savvasdalkitsis.jsonmerger.JsonMerger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


open class BootstrapTask : DefaultTask() {

    private fun formatDate(date: Date?) = with(date ?: Date()) {
        SimpleDateFormat("yyyy-MM-dd").format(this)
    }

    private fun hash(file: ByteArray): String {
        return MessageDigest.getInstance("SHA-512").digest(file).fold("", { str, it -> str + "%02x".format(it) }).toUpperCase()
    }

    private fun getBootstrap(filename: String): JSONArray? {
		var file = File(filename)
		if(file.exists())
		{
			var bootstrapFile = file.readText();
		    return JSONObject("{\"plugins\":$bootstrapFile}").getJSONArray("plugins")
		}
		else
		{
            var empty = "[]";
            return JSONObject("{\"plugins\":$empty}").getJSONArray("plugins")
		}
    }

    @TaskAction
    fun boostrap() {
        if (project == project.rootProject) {
            val bootstrapDir = File("${project.projectDir}")
            val bootstrapReleaseDir = File("${project.projectDir}/release")

            bootstrapReleaseDir.mkdirs()

            val plugins = ArrayList<JSONObject>()
            val baseBootstrap = getBootstrap("$bootstrapDir/plugins.json") ?: throw RuntimeException("Base bootstrap is null!")

            project.subprojects.forEach {
                if (it.project.properties.containsKey("PluginName") && it.project.properties.containsKey("PluginDescription")) {
                    var pluginAdded = false
                    val plugin = it.project.tasks.get("jar").outputs.files.singleFile

                    val releases = ArrayList<JsonBuilder>()

                    releases.add(JsonBuilder(
                            "version" to it.project.version,
                            "requires" to ProjectVersions.apiVersion,
                            "date" to formatDate(Date()),
                            "url" to "https://github.com/ImNoOSRS/plugins/blob/master/release/${it.project.name}-${it.project.version}.jar?raw=true",
                            "sha512sum" to hash(plugin.readBytes())
                    ))

                    val pluginObject = JsonBuilder(
                            "name" to it.project.extra.get("PluginName"),
                            "id" to nameToId(it.project.extra.get("PluginName") as String),
                            "description" to it.project.extra.get("PluginDescription"),
                            "provider" to it.project.extra.get("PluginProvider"),
                            "projectUrl" to it.project.extra.get("ProjectUrl"),
                            "releases" to releases.toTypedArray()
                    ).jsonObject()

                    for (i in 0 until baseBootstrap.length()) {
                        val item = baseBootstrap.getJSONObject(i)

                        if (!item.get("id").equals(nameToId(it.project.extra.get("PluginName") as String))) {
                            continue
                        }

                        if (it.project.version.toString() in item.getJSONArray("releases").toString()) {
                            pluginAdded = true
                            plugins.add(item)
                            break
                        }

                        plugins.add(JsonMerger(arrayMergeMode = JsonMerger.ArrayMergeMode.MERGE_ARRAY).merge(item, pluginObject))
                        pluginAdded = true
                    }

                    if (!pluginAdded) {
                        plugins.add(pluginObject)
                    }

                    plugin.copyTo(Paths.get(bootstrapReleaseDir.toString(), "${it.project.name}-${it.project.version}.jar").toFile(), overwrite = true)
                }
            }

            File(bootstrapDir, "plugins.json").printWriter().use { out ->
                out.println(plugins.toString())
            }
        }

    }
}