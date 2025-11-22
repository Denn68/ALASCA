package fr.sorbonne_u.components.hem2025e2.equipments.vacuum_cleaner.mil.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

// -----------------------------------------------------------------------------
/**
 * The abstract class <code>AbstractVacuumCleanerEvent</code> enforces a common
 * type for all vacuum cleaner simulation events.
 *
 * <p><strong>Description</strong></p>
 * * <p><strong>Implementation Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * <p><strong>Invariants</strong></p>
 * * <pre>
 * invariant	{@code true}	// no more invariant
 * </pre>
 * * @author	Team DeMoh
 */
public class			AbstractVacuumCleanerEvent
extends		ES_Event
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * used to create an event used by the vacuum cleaner simulation model.
	 * * <p><strong>Contract</strong></p>
	 * * <pre>
	 * pre	{@code timeOfOccurrence != null}
	 * post	{@code getTimeOfOccurrence().equals(timeOfOccurrence)}
	 * post	{@code content == null || getEventInformation().equals(content)}
	 * </pre>
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content (data) associated with the event.
	 */
	public				AbstractVacuumCleanerEvent(
		Time timeOfOccurrence,
		EventInformationI content
		)
	{
		super(timeOfOccurrence, content);
	}
}
// -----------------------------------------------------------------------------