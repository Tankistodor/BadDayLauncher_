/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmc.minecraft;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import jmc.minecraft.utils.ConfigLoaderCore;
import jmc.minecraft.utils.GlobalVar;
import jmc.minecraft.utils.MCGameRuner;
import jmc.minecraft.utils.Updater;
import jmc.minecraft.utils.Utils;

/**
 * 
 * @author DimanA90
 */
public class RunGame {

	public static void Init(final JProgressBar progressCurrent,
			final JProgressBar progressBarTotal) {

		Thread myThready = new Thread(new Runnable() {
			public void run() {

				ConfigLoaderCore cfsaver = new ConfigLoaderCore();
				cfsaver.saveUserConfig();
				Updater gup = new Updater();
				gup.Init(progressCurrent, progressBarTotal);
				MCGameRuner grun = new MCGameRuner();
				grun.LetsGame(true);
				System.exit(0);
			}
		});
		myThready.start();

	}

}
