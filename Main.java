package application;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class Main extends Application {
	private final Object obj = new Object();
	private List<File> selectedFiles;

	void displayMetadata(){

		try {
	        for (File file : selectedFiles) {
	            readMetaData(file.toURI().toString());
	            synchronized(obj){
	               obj.wait(100);
	            }
	        }
	    } catch (InterruptedException ex) {
	    }
	    System.gc();
	}
	/**
	 * Read a Media source metadata
	 * Note: Sometimes the was unable to extract the metadata especially when
	 * i have selected large number of files reasons i don't known why
	 * @param mediaURI Media file URI
	 */
	private void readMetaData(String mediaURI){
	    final MediaPlayer mp= new MediaPlayer(new Media(mediaURI));
	    mp.setOnReady(new Runnable() {

	        @Override
	        public void run() {
	            String artistName=(String) mp.getMedia().getMetadata().get("artist");
	            System.out.println(artistName);
	            synchronized(obj){//this is required since mp.setOnReady creates a new thread and our loopp  in the main thread
	                obj.notify();// the loop has to wait unitl we are able to get the media metadata thats why use .wait() and .notify() to synce the two threads(main thread and MediaPlayer thread)
	            }
	        }
	    });
	}


	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();


			Button Select = new Button("Select ...");
			root.setCenter(Select);


			Select.setOnAction(e->{

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Music Files");
                selectedFiles = fileChooser.showOpenMultipleDialog(primaryStage);
                displayMetadata();


			});


			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());




			primaryStage.setScene(scene);
			primaryStage.setTitle("Get Metadata");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
