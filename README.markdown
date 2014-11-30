  Welcome to the rice-lb-extensions Project, we hope you enjoy your time
on this project site.  We've tried to assemble some
great user documentation and developer information, and
we're really excited that you've taken the time to visit
this site.

What is rice-lb-extensions
=================

  Liquibase extensions proprietary to [Kuali Rice](http://rice.kuali.org) software. These are mostly custom refactorings that apply to
the [Kuali Rice](http://rice.kuali.org) platform. Here's an example of what we're talking about:

    <changeSet id="tst-attr-defn-cn" author="lb-ext" context="custom-namespace">
        <kim:attributeDefinition label="Attr. Label" namespace="KUALI" name="Test Attr Def" component="org.kuali.rice.kim.bo.impl.KimAttributes" active="Y"/>
    </changeSet>

    <changeSet id="create-type:alt-schema" author="lb-ext" context="alternate-schema">
        <kim:createType namespace="KFS-TEM" name="lb=ext Type" serviceName="Amend Service">
          <kim:attribute name="beanName" type="Default" value="Test Responsibility Attr"/>
        </kim:createType>
    </changeSet>


* Checkout our [G+ Page](http://plus.google.com/101577324918122329049/)
