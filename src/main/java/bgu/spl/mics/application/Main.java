package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application.It will parse the input file,
 * and run the system.
 * In the end, it will output a JSON.
 */
public class Main {
	public static CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	private static CountDownLatch countDownLatch;
	public static void main(String[] args) {
		 countDownLatch = new CountDownLatch(4);
		try {
			// create Gson instance
			Gson gson = new Gson();

			// create a reader
			Reader reader = Files.newBufferedReader(Paths.get(args[0]));

			// convert JSON string to Input Object
			Input in = gson.fromJson(reader, Input.class);

			Ewoks.getInstance().setter(in.getEwoks());

			LeiaMicroservice leia=new LeiaMicroservice(in.getAttacks());
			C3POMicroservice c3PO=new C3POMicroservice();
			HanSoloMicroservice hanSolo=new HanSoloMicroservice();
			R2D2Microservice r2D2=new R2D2Microservice(in.getR2D2());
			LandoMicroservice lando=new LandoMicroservice(in.getLando());

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

			Leia.join();
			C3PO.join();
			HanSolo.join();
			R2D2.join();
			Lando.join();

			// create Gson instance
			Gson gson1 = new Gson();

			// create a writer
			Writer writer = Files.newBufferedWriter(Paths.get(args[1]));

			// convert Diary to JSON file
			gson1.toJson(Diary.getInstance() , writer);

			// close writer
			writer.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}