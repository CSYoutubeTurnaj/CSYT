/*
 * Made by IToncek
 *
 * Copyright (c) 2023.
 */

package space.itoncek.csyt;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public abstract class DRMLib {
    public DRMLib() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
					URL url = new URL("https://drmblock.itoncek.space");
					try (Scanner sc = new Scanner(url.openStream())) {
						while (sc.hasNextLine()) {
							System.out.println(sc.nextLine());
						}
					}
                } catch (IOException ignored) {
                    return;
                }
                System.out.println("*");
                //callback();
			}
        }, 0, 600000);
    }

    public static boolean test() {
        try {
            URL url = new URL("http://drmblock.itoncek.space");
            URLConnection urlConnection = url.openConnection();

            urlConnection.connect();
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    public abstract void callback();
}