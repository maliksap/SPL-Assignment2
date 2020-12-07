package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		try {
			// create Gson instance
			Gson gson = new Gson();

			// create a reader
			Reader reader = Files.newBufferedReader(Paths.get(args[0]));

			// convert JSON string to User object
			Input in = gson.fromJson(reader, Input.class);

			Ewoks.getInstance().setter(in.getEwoks());

			CountDownLatch countDownLatch = new CountDownLatch(4);

			LeiaMicroservice leia=new LeiaMicroservice(in.getAttacks());
			C3POMicroservice c3PO=new C3POMicroservice(countDownLatch);
			HanSoloMicroservice hanSolo=new HanSoloMicroservice(countDownLatch);
			R2D2Microservice r2D2=new R2D2Microservice(in.getR2D2(),countDownLatch);
			LandoMicroservice lando=new LandoMicroservice(in.getLando(),countDownLatch);

			Thread Leia=new Thread(leia);
			Thread C3PO=new Thread(c3PO);
			Thread HanSolo=new Thread(hanSolo);
			Thread R2D2=new Thread(r2D2);
			Thread Lando=new Thread(lando);

			C3PO.start();
			HanSolo.start();
			R2D2.start();
			Lando.start();
			countDownLatch.await();
			Leia.start();



			// close reader
			reader.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}