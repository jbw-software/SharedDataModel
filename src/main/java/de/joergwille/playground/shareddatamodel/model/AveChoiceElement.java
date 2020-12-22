/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.model;

import java.awt.Dimension;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author joerg
 */
public class AveChoiceElement extends JComboBox<String> {

    public AveChoiceElement() {
        super();
        setOpaque(true);
        System.out.println("AveChoiceElement Constructor called.");
    }

    public AveChoiceElement(ComboBoxModel<String> model) {
        super(model);
        setOpaque(true);
        System.out.println("AveChoiceElement Constructor called.");
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(40, d.height);
    }
}
