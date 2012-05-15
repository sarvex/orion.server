/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.orion.server.tests.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.eclipse.orion.server.core.resources.Property;
import org.eclipse.orion.server.core.resources.ResourceShape;
import org.eclipse.orion.server.core.resources.ResourceShapeFactory;
import org.hamcrest.collection.IsArrayContaining;
import org.junit.Test;

/**
 * Tests for {@link ResourceShape}.
 */
public class ResourceShapeTest {
	@Test
	public void testDefaultResourceShape() throws Exception {
		// when
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, null);

		// then
		Property[] properties = resourceShape.getProperties();
		assertAllPropertiesExists(properties);
	}

	@Test
	public void testAllProperties() throws Exception {
		// when
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, Property.ALL_PROPERTIES.getName());

		// then
		Property[] properties = resourceShape.getProperties();
		assertAllPropertiesExists(properties);
	}

	private void assertAllPropertiesExists(Property[] properties) {
		assertEquals(5, properties.length);
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.STRING_PROPERTY)));
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.INT_PROPERTY)));
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.BOOLEAN_PROPERTY)));
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.LOCATION_PROPERTY)));
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.RESOURCE_PROPERTY)));
	}

	@Test
	public void testSingleProperty() throws Exception {
		// when
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, TestResource.STRING_PROPERTY_NAME);

		// then
		Property[] properties = resourceShape.getProperties();
		assertEquals(1, properties.length);
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.STRING_PROPERTY)));
	}

	@Test
	public void testCommaSeparatedProperties() throws Exception {
		// when
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, TestResource.STRING_PROPERTY_NAME + "," + TestResource.BOOLEAN_PROPERTY_NAME);

		// then
		Property[] properties = resourceShape.getProperties();
		assertEquals(2, properties.length);
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.STRING_PROPERTY)));
		assertThat(properties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.BOOLEAN_PROPERTY)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidProperty() throws Exception {
		// when
		ResourceShapeFactory.createResourceShape(TestResource.class, "invalid");
	}

	@Test
	public void testDefaultResourceShapeForNestedResource() throws Exception {
		// given
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, null);
		Property[] properties = resourceShape.getProperties();
		Property resourceProperty = getPropertyWithName(properties, TestResource.RESOURCE_PROPERTY_NAME);

		// when
		ResourceShape nestedResourceShape = resourceProperty.getResourceShape();

		// then
		Property[] nestedProperties = nestedResourceShape.getProperties();
		assertEquals(1, nestedProperties.length);
		assertThat(nestedProperties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.LOCATION_PROPERTY)));
	}

	@Test
	public void testModifiedResourceShapeForNestedResource() throws Exception {
		// given
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, TestResource.RESOURCE_PROPERTY_NAME + "{" + TestResource.STRING_PROPERTY_NAME + ResourceShape.SEPARATOR + TestResource.INT_PROPERTY_NAME + "}");
		Property[] properties = resourceShape.getProperties();
		Property resourceProperty = getPropertyWithName(properties, TestResource.RESOURCE_PROPERTY_NAME);

		// when
		ResourceShape nestedResourceShape = resourceProperty.getResourceShape();

		// then
		Property[] nestedProperties = nestedResourceShape.getProperties();
		assertEquals(2, nestedProperties.length);
		assertThat(nestedProperties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.STRING_PROPERTY)));
		assertThat(nestedProperties, IsArrayContaining.hasItemInArray(PropertyMatcher.isPropertyNameEqual(TestResource.INT_PROPERTY)));
	}

	@Test
	public void testAllPropertiesForNestedResource() throws Exception {
		// given
		ResourceShape resourceShape = ResourceShapeFactory.createResourceShape(TestResource.class, TestResource.RESOURCE_PROPERTY_NAME + "{" + ResourceShape.WILDCARD + "}");
		Property[] properties = resourceShape.getProperties();
		Property resourceProperty = getPropertyWithName(properties, TestResource.RESOURCE_PROPERTY_NAME);

		// when
		ResourceShape nestedResourceShape = resourceProperty.getResourceShape();

		// then
		Property[] nestedProperties = nestedResourceShape.getProperties();
		assertAllPropertiesExists(nestedProperties);
	}

	private Property getPropertyWithName(Property[] properties, String name) {
		for (Property property : properties) {
			if (property.getName().equals(name))
				return property;
		}
		throw new IllegalArgumentException(name + " not found");
	}
}