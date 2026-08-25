package com.salamasoa.salamasoa_app;

import com.formdev.flatlaf.FlatLightLaf;
import com.salamasoa.salamasoa_app.view.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;

@SpringBootApplication
public class SalamasoaAppApplication {

	public static void main(String[] args) {
		// 1. Initialiser le thème graphique
		FlatLightLaf.setup();

		// 2. Démarrer Spring Boot en mode GUI (headless = false)
		ConfigurableApplicationContext context = new SpringApplicationBuilder(SalamasoaAppApplication.class)
				.headless(false)
				.run(args);

		// 3. Afficher la fenêtre Swing
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = context.getBean(MainFrame.class);
			mainFrame.setVisible(true);
		});
	}

}
