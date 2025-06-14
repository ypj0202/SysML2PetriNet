/**
 */
package petrinet.util;

import java.util.Map;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.EObjectValidator;

import petrinet.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Validator</b> for the model.
 * <!-- end-user-doc -->
 * @see petrinet.PetrinetPackage
 * @generated
 */
public class PetrinetValidator extends EObjectValidator {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final PetrinetValidator INSTANCE = new PetrinetValidator();

	/**
	 * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Diagnostic#getSource()
	 * @see org.eclipse.emf.common.util.Diagnostic#getCode()
	 * @generated
	 */
	public static final String DIAGNOSTIC_SOURCE = "petrinet";

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PetrinetValidator() {
		super();
	}

	/**
	 * Returns the package of this validator switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EPackage getEPackage() {
	  return PetrinetPackage.eINSTANCE;
	}

	/**
	 * Calls <code>validateXXX</code> for the corresponding classifier of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics, Map<Object, Object> context) {
		switch (classifierID) {
			case PetrinetPackage.PETRI_NET:
				return validatePetriNet((PetriNet)value, diagnostics, context);
			case PetrinetPackage.NODE:
				return validateNode((Node)value, diagnostics, context);
			case PetrinetPackage.PLACE:
				return validatePlace((Place)value, diagnostics, context);
			case PetrinetPackage.TRANSITION:
				return validateTransition((Transition)value, diagnostics, context);
			case PetrinetPackage.ARC:
				return validateArc((Arc)value, diagnostics, context);
			default:
				return true;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePetriNet(PetriNet petriNet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(petriNet, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNode(Node node, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(node, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePlace(Place place, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(place, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateTransition(Transition transition, DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(transition, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateArc(Arc arc, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(arc, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(arc, diagnostics, context);
		if (result || diagnostics != null) result &= validateArc_ArcTypeConstraint(arc, diagnostics, context);
		return result;
	}

	/**
	 * Validates the ArcTypeConstraint constraint of '<em>Arc</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateArc_ArcTypeConstraint(Arc arc, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean isValid = true;
		
		// Get source and target nodes
		Node source = arc.getSource();
		Node target = arc.getTarget();
		
		// Check if source is a Place
		if (source instanceof Place) {
			// If source is a Place, target must be a Transition
			if (!(target instanceof Transition)) {
				isValid = false;
				if (diagnostics != null) {
					diagnostics.add(
						createDiagnostic(
							Diagnostic.ERROR,
							DIAGNOSTIC_SOURCE,
							0,
							"Arc from Place to non-Transition is not allowed",
							new Object[] { "ArcTypeConstraint", getObjectLabel(arc, context) },
							new Object[] { arc },
							context));
				}
			}
		}
		// Check if source is a Transition
		else if (source instanceof Transition) {
			// If source is a Transition, target must be a Place
			if (!(target instanceof Place)) {
				isValid = false;
				if (diagnostics != null) {
					diagnostics.add(
						createDiagnostic(
							Diagnostic.ERROR,
							DIAGNOSTIC_SOURCE,
							0,
							"Arc from Transition to non-Place is not allowed",
							new Object[] { "ArcTypeConstraint", getObjectLabel(arc, context) },
							new Object[] { arc },
							context));
				}
			}
		}
		
		return isValid;
	}

	/**
	 * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		// TODO
		// Specialize this to return a resource locator for messages specific to this validator.
		// Ensure that you remove @generated or mark it @generated NOT
		return super.getResourceLocator();
	}

} //PetrinetValidator
