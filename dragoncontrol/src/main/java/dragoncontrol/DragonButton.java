package dragoncontrol;

import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class DragonButton extends Button {

    private String hooverOvertext;
    private Text outputField;
    private String name;
    private String originalText;

    private DragonButton(String create) {
        super(create);
        this.hooverOvertext = hooverOvertext;

        this.setOnMouseEntered(event -> {
            this.originalText = outputField.getText();
            outputField.setText(hooverOvertext);
            }
        );
        this.setOnMouseExited(event -> {outputField.setText(originalText);});
    }


    public static class ButtonBuilder{
        private String hooverOvertext;
        private Text outputField = new Text();
        private String name;

        public ButtonBuilder hooverOvertext(String hooverOvertext){
            this.hooverOvertext = hooverOvertext;
            return this;
        }

        public ButtonBuilder textOutputField(Text outputField){
            this.outputField = outputField;
            return this;
        }

        public ButtonBuilder setButtonName(String name){
            this.name = name;
            return this;
        }



        public DragonButton build(){

            DragonButton temp = new DragonButton(this.name);
            temp.hooverOvertext = this.hooverOvertext;
            temp.outputField = this.outputField;
            temp.name = this.name;
            return temp;
        }

    }
}
