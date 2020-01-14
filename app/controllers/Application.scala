package controllers

import java.io.InputStreamReader
import javax.script.ScriptEngineManager
import play.api.mvc.{ AbstractController, ControllerComponents }

import com.google.inject.Inject
import play.api.Environment
import play.api.mvc._

class Application @Inject()( env: Environment, controllerComponents: ControllerComponents) extends AbstractController(controllerComponents) {

  def index = Action {
    // Pass 'null' to force the correct class loader. Without passing any param,
    // the "nashorn" JavaScript engine is not found by the `ScriptEngineManager`.
    //
    // See: https://github.com/playframework/playframework/issues/2532
    val engine = new ScriptEngineManager(null).getEngineByName("nashorn")

    if (engine == null) {
      BadRequest("Nashorn script engine not found. Are you using JDK 8?")
    } else {
      // React expects `window` or `global` to exist. Create a `global` pointing
      // to Nashorn's context to give React a place to define its global
      // namespace.
      engine.eval("var global = this;")

      // Define `console.log`, etc. to send messages to Nashorn's global `print`
      // function so the messages are written to standard out.
      engine.eval("var console = {error: print, log: print, warn: print};")

      // Evaluate vue and the application code.
      engine.eval("var process = { env: { VUE_ENV: 'server', NODE_ENV: 'production' }}; this.global = { process: process };");
      engine.eval(new InputStreamReader(env.classLoader.getResource("public/javascripts/vue.js").openStream()))
      engine.eval(new InputStreamReader(env.classLoader.getResource("public/javascripts/vue-server-render-basic.js").openStream()))
      // engine.eval(new InputStreamReader(env.classLoader.getResource("public/javascripts/app.js").openStream()))
      // engine.eval(new InputStreamReader(env.classLoader.getResource("app/assets/vue/components/greeting-form.js").openStream()))


      //Ok(views.html.index("React on Play") {
      //  play.twirl.api.Html(engine.eval(new InputStreamReader(env.classLoader.getResource("public/javascripts/app.js").openStream())).toString)
      //})
    Ok(views.html.index(engine.eval(new InputStreamReader(env.classLoader.getResource("public/javascripts/app.js").openStream())).toString))

    }
  }

}