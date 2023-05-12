//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package ru.nlatysh.common.models;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ru.nlatysh.common.models package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ru.nlatysh.common.models
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CrackHashManagerRequest }
     * 
     * @return
     *     the new instance of {@link CrackHashManagerRequest }
     */
    public CrackHashManagerRequest createCrackHashManagerRequest() {
        return new CrackHashManagerRequest();
    }

    /**
     * Create an instance of {@link CrackHashWorkerResponse }
     * 
     * @return
     *     the new instance of {@link CrackHashWorkerResponse }
     */
    public CrackHashWorkerResponse createCrackHashWorkerResponse() {
        return new CrackHashWorkerResponse();
    }

    /**
     * Create an instance of {@link CrackHashManagerRequest.Alphabet }
     * 
     * @return
     *     the new instance of {@link CrackHashManagerRequest.Alphabet }
     */
    public CrackHashManagerRequest.Alphabet createCrackHashManagerRequestAlphabet() {
        return new CrackHashManagerRequest.Alphabet();
    }

    /**
     * Create an instance of {@link CrackHashWorkerResponse.Answers }
     * 
     * @return
     *     the new instance of {@link CrackHashWorkerResponse.Answers }
     */
    public CrackHashWorkerResponse.Answers createCrackHashWorkerResponseAnswers() {
        return new CrackHashWorkerResponse.Answers();
    }

}
