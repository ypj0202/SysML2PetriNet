/**
 */
package petrinet.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import petrinet.Arc;
import petrinet.Node;
import petrinet.PetrinetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link petrinet.impl.NodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link petrinet.impl.NodeImpl#getOutgoingArcs <em>Outgoing Arcs</em>}</li>
 *   <li>{@link petrinet.impl.NodeImpl#getIncomingArcs <em>Incoming Arcs</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class NodeImpl extends MinimalEObjectImpl.Container implements Node {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getOutgoingArcs() <em>Outgoing Arcs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutgoingArcs()
	 * @generated
	 * @ordered
	 */
	protected EList<Arc> outgoingArcs;

	/**
	 * The cached value of the '{@link #getIncomingArcs() <em>Incoming Arcs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIncomingArcs()
	 * @generated
	 * @ordered
	 */
	protected EList<Arc> incomingArcs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return PetrinetPackage.Literals.NODE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, PetrinetPackage.NODE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Arc> getOutgoingArcs() {
		if (outgoingArcs == null) {
			outgoingArcs = new EObjectWithInverseResolvingEList<Arc>(Arc.class, this, PetrinetPackage.NODE__OUTGOING_ARCS, PetrinetPackage.ARC__SOURCE);
		}
		return outgoingArcs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<Arc> getIncomingArcs() {
		if (incomingArcs == null) {
			incomingArcs = new EObjectWithInverseResolvingEList<Arc>(Arc.class, this, PetrinetPackage.NODE__INCOMING_ARCS, PetrinetPackage.ARC__TARGET);
		}
		return incomingArcs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutgoingArcs()).basicAdd(otherEnd, msgs);
			case PetrinetPackage.NODE__INCOMING_ARCS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingArcs()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				return ((InternalEList<?>)getOutgoingArcs()).basicRemove(otherEnd, msgs);
			case PetrinetPackage.NODE__INCOMING_ARCS:
				return ((InternalEList<?>)getIncomingArcs()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case PetrinetPackage.NODE__NAME:
				return getName();
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				return getOutgoingArcs();
			case PetrinetPackage.NODE__INCOMING_ARCS:
				return getIncomingArcs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case PetrinetPackage.NODE__NAME:
				setName((String)newValue);
				return;
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				getOutgoingArcs().clear();
				getOutgoingArcs().addAll((Collection<? extends Arc>)newValue);
				return;
			case PetrinetPackage.NODE__INCOMING_ARCS:
				getIncomingArcs().clear();
				getIncomingArcs().addAll((Collection<? extends Arc>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case PetrinetPackage.NODE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				getOutgoingArcs().clear();
				return;
			case PetrinetPackage.NODE__INCOMING_ARCS:
				getIncomingArcs().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case PetrinetPackage.NODE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case PetrinetPackage.NODE__OUTGOING_ARCS:
				return outgoingArcs != null && !outgoingArcs.isEmpty();
			case PetrinetPackage.NODE__INCOMING_ARCS:
				return incomingArcs != null && !incomingArcs.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //NodeImpl
