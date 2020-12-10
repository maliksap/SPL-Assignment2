package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import bgu.spl.mics.application.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.concurrent.CountDownLatch;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.ls.LSOutput;

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

//			MessageBusImpl.getInstance();  // initialize messageBus before threads starts to run added by sapir

//			Diary.getInstance(); // initialize messageBus before threads starts to run

			Thread Leia=new Thread(leia);
			Thread C3PO=new Thread(c3PO);
			Thread HanSolo=new Thread(hanSolo);
			Thread R2D2=new Thread(r2D2);
			Thread Lando=new Thread(lando);

			Leia.setName("Leia-Thread");
			C3PO.setName("C3PO-Thread");
			HanSolo.setName("HanSolo-Thread");
			R2D2.setName("R2D2-Thread");
			Lando.setName("Lando-Thread");

			C3PO.start();
			HanSolo.start();
			R2D2.start();
			Lando.start();
			countDownLatch.await();
			Leia.start();

			// close reader
			reader.close();

			Leia.join();
			C3PO.join();
			HanSolo.join();
			R2D2.join();
			Lando.join();

			Gson gsonOutput = new Gson();
			gsonOutput.toJson("totalAttacks:" + (Diary.getInstance().getTotalAttacks()) );
			System.out.println("totalAttacks:" + (Diary.getInstance().getTotalAttacks()) );
			gsonOutput.toJson("HanSoloFinish:" + (Diary.getInstance().getHanSoloFinish()) );
			System.out.println("HanSoloFinish:" + (Diary.getInstance().getHanSoloFinish()) );
			gsonOutput.toJson("C3POFinish:" + (Diary.getInstance().getC3POFinish()) );
			System.out.println("C3POFinish:" + (Diary.getInstance().getC3POFinish()) );
			gsonOutput.toJson("R2D2Deactivate:" + (Diary.getInstance().getR2D2Deactivate()) );
			System.out.println("R2D2Deactivate:" + (Diary.getInstance().getR2D2Deactivate()) );
			gsonOutput.toJson("LeiaTerminate:" + (Diary.getInstance().getLeiaTerminate()) );
			System.out.println("LeiaTerminate:" + (Diary.getInstance().getLeiaTerminate()) );
			gsonOutput.toJson("HanSoloTerminate:" + (Diary.getInstance().getHanSoloTerminate()) );
			System.out.println("HanSoloTerminate:" + (Diary.getInstance().getHanSoloTerminate()) );
			gsonOutput.toJson("C3POTerminate:" + (Diary.getInstance().getC3POTerminate()) );
			System.out.println("C3POTerminate:" + (Diary.getInstance().getC3POTerminate()) );
			gsonOutput.toJson("R2D2Terminate:" + (Diary.getInstance().getR2D2Terminate()) );
			System.out.println("R2D2Terminate:" + (Diary.getInstance().getR2D2Terminate()) );
			gsonOutput.toJson("LandoTerminate:" + (Diary.getInstance().getLandoTerminate()) );
			System.out.println("LandoTerminate:" + (Diary.getInstance().getLandoTerminate()) );
			try {
				FileWriter file = new FileWriter("output.json");
				file.write(gson.toString());
				file.close();
			} catch (IOException e) {
				System.out.println("JSON file created: " +gson);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}