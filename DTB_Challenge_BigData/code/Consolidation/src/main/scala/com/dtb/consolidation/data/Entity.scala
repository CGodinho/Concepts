package com.dtb.consolidation.data

import scala.util.matching.Regex


/**
 * @author CGodinho
 *
 * This trait is used as a basic object for all datasets.
 */
trait Entity {}

/**
 * @author CGodinho
 *
 * Trait used for the datasets companion objects.
 */
trait EntityCompanion[A <: Entity] {

    implicit def self: EntityCompanion[A] = this

    /**
     * Builds a key for an entity.
     *
     * @param record  - record under analysis;
     * @param fields  - fields holder under analysis, with parsed tokens;
     * @return - string with entity key.
     */
    def key(record : String, fields : List[Int]) : String

    /**
     * Returns the expected number of fields of an raw Entity record.
     *
     * @return - # of fields
     */
    def size : Int

    /**
     * Returns the char used as separator by the Entity.
     *
     * @return - Separator char.
     */
    def separator : Char

    /**
     * Returns the char used as quote by the Entity.
     *
     * @return - Quote char.
     */
    def quote : Char

    /**
     * Returns a compiled Regular Expression that validates a raw Entity record.
     *
     * @return - compiled regular expression
     */
    def validate : Regex

    /**
     * Builds a new Entity object.
     *
     * @param record  - record under analysis;
     * @param fields  - fields holder under analysis, with parsed tokens;
     * @return - the new Entity object.
     */
    def factory(record : String, fields : List[Int]) : Entity
}