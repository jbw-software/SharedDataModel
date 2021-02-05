/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.joergwille.playground.shareddatamodel.swing.model;

/**
 *
 * @author joerg
 * @param <E>
 */
public interface UpdateObservable<E> {

    public void addUpdateListener(UpdateListener<E> listener);

    public void removeUpdateListener(UpdateListener<E> listener);
}
