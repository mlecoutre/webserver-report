package org.mat.samples.mongodb.vo;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: mlecoutre
 * Date: 07/12/12
 * Time: 10:25
 */
@XmlRootElement
public class Scheduler {

    private String applicationName;
    private String as;
    private Integer interval;
    private String status;

}
