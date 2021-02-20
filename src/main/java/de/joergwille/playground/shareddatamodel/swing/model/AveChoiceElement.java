/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.swing.model;

import javax.swing.JComboBox;

/**
 *
 * @author joerg
 */
@SuppressWarnings("serial")
public class AveChoiceElement extends JComboBox<String> {

    public AveChoiceElement() {
        this(null);
    }

    public AveChoiceElement(AveSharedComboBoxModel<String> model) {
        super(model);
        setOpaque(true);
//        System.out.println("AveChoiceElement Constructor called.");
    }
    
    @Override
    public void setPrototypeDisplayValue(String prototypeDisplayValue) {
        super.setPrototypeDisplayValue(prototypeDisplayValue);
//        System.out.println("PrototypeDisplayValue of 'AveChoiceElement' is set to : " + prototypeDisplayValue);        
    }
}
