package com.example.felipeaboytes.appinventario

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    //val mcrypt = MCrypt()
    var hilo: ObtenerWebService? = null
    var nomAlumno : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun btnReg(v:View){
        var nc: String=editTextnocon.text.toString()
        var con: String= editText2con.text.toString()
        var tel: String="46123"
        hilo = ObtenerWebService()
        hilo?.execute("Nojala", "0", nc, con,tel)

    }

    //-------------Servicios web de registro-------------------------------------------------
    inner class ObtenerWebService() : AsyncTask<String, String, String>() {
        var Nocontrol: String = ""


        override fun doInBackground(vararg params: String?): String {
            var url: URL? = null
            var devuelve = ""
            try {
                val urlConn: HttpURLConnection
                val printout: DataOutputStream
                val input: DataInputStream

                url = URL("http://192.168.64.2/tecnRoque/RegAlumno.php")

                //  Abrimos la conexión hacia el servicio web alojado en el servidor
                urlConn = url.openConnection() as HttpURLConnection
                urlConn.doInput = true
                urlConn.doOutput = true
                urlConn.useCaches = false
                urlConn.setRequestProperty("Content-Type", "application/json")
                urlConn.setRequestProperty("Accept", "application/json")
                urlConn.connect()
                // Creando parametros que vamos a enviar
                val jsonParam = JSONObject()


                val nc = (params[2]) //MCrypt.bytesToHex(mcrypt.encrypt(params[2]));
                val co =(params[3])//MCrypt.bytesToHex(mcrypt.encrypt(params[3]));
                val te = (params[4])//MCrypt.bytesToHex(mcrypt.encrypt(params[4]))
                Log.d("Zazueta",params[2] + " = " + nc)
                Nocontrol = params[2].toString()
                jsonParam.put("NoCtrl",nc)
                jsonParam.put("Con",co )
                jsonParam.put("Tlf", te)

                //Envio de parametros por el método post
                val os = urlConn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                // Escribe los datos a través de los métodos flush() y write()
                Log.d("ZAZUETA1", jsonParam.toString())
                writer.write(jsonParam.toString())
                writer.flush()
                writer.close()
                val respuesta = urlConn.responseCode

                val result = StringBuilder()
                // Preguntamos si se pudo conectar al servidor con exito
                if (respuesta == HttpURLConnection.HTTP_OK) {
                    // El siguiente proceso de hace por que JSONObject necesita un string para
                    // concatenar lo que envio el servicio web de regreso qu es un JSON
                    val inStream: InputStream = urlConn.inputStream
                    val isReader = InputStreamReader(inStream)
                    val bReader = BufferedReader(isReader)
                    var tempStr: String?
                    while (true) {
                        tempStr = bReader.readLine()
                        if (tempStr == null) {
                            break
                        }
                        result.append(tempStr)
                    }
                    urlConn.disconnect()
                    devuelve = result.toString() // Regresa un JSON al método onPostExecute
                }
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return devuelve
        }


        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            var ida :  String = "";
            var tit: String="";
            var desc: String= "";
            var fecp: String = "";
            var fecf : String = "";


            val devuelve = ""
            var suc: String
            var msg: String
            // var codErr: String=""
            Log.d("ZAZUETA",s)

            try {
                val respuestaJSON = JSONObject(s.toString())
                val resultJSON = respuestaJSON.getString("success") //Obtiene el primer campo de JSON que es string y se llama estado

                val msgJSON = respuestaJSON.getString("message")
                nomAlumno = (respuestaJSON.getString("NomAlumno")) //desencripta(String(mcrypt.decrypt(respuestaJSON.getString("NomAlumno"))))
                val avisosJSON = respuestaJSON.getJSONArray("aviso")
                Log.d("Zazueta",nomAlumno)

                when (resultJSON) {

                    "200"   // Se inserto correctamente uno nuevo
                    -> {
                        // Insert a sqlite alumno(nocontrol,nomalumno,nip)
                        // Recorrer el arreglo de avisos y insertarlo en sqlite
                        if (avisosJSON.length() >= 1) {
                            for (i in 0 until avisosJSON.length()) {
                                ida= (avisosJSON.getJSONObject(i).getString("idaviso"))//desencripta(String(mcrypt.decrypt(avisosJSON.getJSONObject(i).getString("idaviso"))))
                                tit = (avisosJSON.getJSONObject(i).getString("titulo"))//desencripta(String(mcrypt.decrypt(avisosJSON.getJSONObject(i).getString("titulo"))))
                                desc= (avisosJSON.getJSONObject(i).getString("descripcion"))//desencripta(String(mcrypt.decrypt(avisosJSON.getJSONObject(i).getString("descripcion"))))
                                fecp = (avisosJSON.getJSONObject(i).getString("fechapub"))//desencripta(String(mcrypt.decrypt(avisosJSON.getJSONObject(i).getString("fechapub"))))
                                fecf = (avisosJSON.getJSONObject(i).getString("fechafin"))//desencripta(String(mcrypt.decrypt(avisosJSON.getJSONObject(i).getString("fechafin"))))
                                Log.d("Zazueta",tit) //<------------Insert
                            }

                        } else {
                            Toast.makeText(baseContext,"No hay avisos", Toast.LENGTH_LONG).show()
                        }
                        Toast.makeText(baseContext,"Success 200: " + msgJSON, Toast.LENGTH_LONG).show()
                        /*doAsync {
                            val registroEntity = RegistroEntity(correo = etCorreoR.text.toString(), nomregistro = etNombreR.text.toString(), contrasena = etPwdR.text.toString(), fechalta = Date())
                            val contactoEntity = ContactoEntity(nomcontacto = "Emergencias", telcasa = "911", telcelular = "911", estado = "E", fechalta = Date(), correo = etCorreoR.text.toString(), recfecha = Date())
                            AppDatabase.getInstance(this@registro)!!.registroDao().insertRegistro(registroEntity)
                            AppDatabase.getInstance(this@registro)!!.contactoDao().insertContacto(contactoEntity)
                        }.execute()
                        val messageJSON1 = respuestaJSON.getString("message")
                        resultado = messageJSON1
                        startActivity(inte)*/
                        // val messageJSON4 = respuestaJSON.getString("message")
                        //Toast.makeText(baseContext,"Succes 201: " + messageJSON4, Toast.LENGTH_LONG).show()

                    }


                    "422"  // Falta Información en el web service
                    -> {
                        val messageJSON4 = respuestaJSON.getString("message")
                        Toast.makeText(baseContext,"Error 422: " + messageJSON4, Toast.LENGTH_LONG).show()
                    }
                    "500"  // Error al insertar el registro
                    -> {
                        val messageJSON3 = respuestaJSON.getString("message")
                        Toast.makeText(baseContext, "Error 500: " +messageJSON3, Toast.LENGTH_LONG).show()
                    }

                }
            } catch (e: JSONException) {
                //Toast.makeText(this@MainActivityReg, "Error:" + e.toString(), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } // fin del web service

    /* fun desencripta(cadena: String): String {
         val n = cadena.indexOf("¡")
         return cadena.substring(0, n)
     }
 */

}

