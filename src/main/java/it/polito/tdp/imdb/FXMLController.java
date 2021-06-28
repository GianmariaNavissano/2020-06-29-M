/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.imdb;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;
	private boolean grafoCreato = false;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnAdiacenti"
    private Button btnAdiacenti; // Value injected by FXMLLoader

    @FXML // fx:id="btnCercaAffini"
    private Button btnCercaAffini; // Value injected by FXMLLoader

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxRegista"
    private ComboBox<Director> boxRegista; // Value injected by FXMLLoader

    @FXML // fx:id="txtAttoriCondivisi"
    private TextField txtAttoriCondivisi; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	if(this.boxAnno.getValue()==null) {
    		this.txtResult.appendText("Selezionare un anno\n");
    		return;
    	}
    	int year = this.boxAnno.getValue();
    	this.model.creaGrafo(year);
    	this.grafoCreato = true;
    	this.txtResult.appendText("Grafo creato!\n# vertici: "+this.model.getNumVertici()+"\n# archi: "+this.model.getNumArchi()+"\n");
    	this.boxRegista.getItems().clear();
    	this.boxRegista.getItems().addAll(this.model.getDirectors());

    }

    @FXML
    void doRegistiAdiacenti(ActionEvent event) {
    	this.txtResult.clear();
    	if(!this.grafoCreato) {
    		this.txtResult.appendText("Creare il grafo\n");
    		return;
    	}
    	Director d = this.boxRegista.getValue();
    	if(d==null) {
    		this.txtResult.appendText("Selezionare un regista\n");
    		return;
    	}
    	this.txtResult.appendText(this.model.getAdiacenti(d));

    }

    @FXML
    void doRicorsione(ActionEvent event) {
    	this.txtResult.clear();
    	if(!this.grafoCreato) {
    		this.txtResult.appendText("Creare il grafo\n");
    		return;
    	}
    	Director d = this.boxRegista.getValue();
    	if(d==null) {
    		this.txtResult.appendText("Selezionare un regista\n");
    		return;
    	}
    	if(this.txtAttoriCondivisi.getText().equals("")) {
    		this.txtResult.appendText("Selezionare un numero massimo di attori condivisi\n");
    		return;
    	}
    	int pesoMax = 0;
    	try {
    		pesoMax = Integer.parseInt(this.txtAttoriCondivisi.getText());
    	}catch(NumberFormatException e) {
    		this.txtResult.appendText("Il numero massimo di attori condivisi deve essere un intero positivo\n");
    		return;
    	}
    	this.txtResult.appendText(this.model.getAffini(d, pesoMax));
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAdiacenti != null : "fx:id=\"btnAdiacenti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCercaAffini != null : "fx:id=\"btnCercaAffini\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxRegista != null : "fx:id=\"boxRegista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtAttoriCondivisi != null : "fx:id=\"txtAttoriCondivisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
   public void setModel(Model model) {
    	
    	this.model = model;
    	
    	this.boxAnno.getItems().add(2004);
    	this.boxAnno.getItems().add(2005);
    	this.boxAnno.getItems().add(2006);
    	
    }
    
}
