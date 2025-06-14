/**
 */
package petrinet;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Place</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link petrinet.Place#getInitialMarking <em>Initial Marking</em>}</li>
 * </ul>
 *
 * @see petrinet.PetrinetPackage#getPlace()
 * @model
 * @generated
 */
public interface Place extends Node {
	/**
	 * Returns the value of the '<em><b>Initial Marking</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Initial Marking</em>' attribute.
	 * @see #setInitialMarking(int)
	 * @see petrinet.PetrinetPackage#getPlace_InitialMarking()
	 * @model default="0"
	 * @generated
	 */
	int getInitialMarking();

	/**
	 * Sets the value of the '{@link petrinet.Place#getInitialMarking <em>Initial Marking</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Initial Marking</em>' attribute.
	 * @see #getInitialMarking()
	 * @generated
	 */
	void setInitialMarking(int value);

} // Place
