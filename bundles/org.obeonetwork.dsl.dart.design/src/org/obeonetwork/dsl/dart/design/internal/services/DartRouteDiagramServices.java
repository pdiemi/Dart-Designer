/*******************************************************************************
 * Copyright (c) 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.dart.design.internal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.obeonetwork.dsl.dart.HTML;
import org.obeonetwork.dsl.dart.Module;
import org.obeonetwork.dsl.dart.Route;
import org.obeonetwork.dsl.dart.design.internal.utils.I18n;
import org.obeonetwork.dsl.dart.design.internal.utils.I18nKeys;

/**
 * Utility services for the route diagram.
 *
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 */
public class DartRouteDiagramServices {
	/**
	 * Returns the name of the newly created route diagram for the given container.
	 *
	 * @param eObject
	 *            The module
	 * @return The name of the route diagram
	 */
	public String newRouteDiagramName(EObject eObject) {
		String containerName = ""; //$NON-NLS-1$
		if (eObject instanceof Module) {
			containerName = ((Module)eObject).getName();
		}
		return I18n.getString(I18nKeys.ROUTE_DIAGRAM_NEW_NAME, containerName).trim();
	}

	/**
	 * Returns the list of all the packages that can be added to the diagram.
	 *
	 * @param eObject
	 *            The currently selected eObject (the project in the background most of the time)
	 * @param containerView
	 *            The container view
	 * @return The list of Dart packages that can be added
	 */
	public List<EObject> getAddableRouteDiagramElements(EObject eObject, DSemanticDecorator containerView) {
		List<EObject> result = new ArrayList<EObject>();

		Session session = SessionManager.INSTANCE.getSession(eObject);
		if (session != null) {
			Collection<Resource> semanticResources = session.getSemanticResources();
			for (Resource resource : semanticResources) {
				if (resource.getURI().isPlatformPlugin() || resource.getURI().isPlatformResource()) {
					TreeIterator<EObject> allContents = resource.getAllContents();
					while (allContents.hasNext()) {
						EObject containedEObject = allContents.next();
						if (containedEObject instanceof Route) {
							result.add(containedEObject);
						} else if (containedEObject instanceof HTML) {
							result.add(containedEObject);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns the label of the route.
	 *
	 * @param eObject
	 *            The route
	 * @return The label of the route
	 */
	public String getRouteLabel(EObject eObject) {
		final String slash = "/"; //$NON-NLS-1$

		if (eObject instanceof Route) {
			Route route = (Route)eObject;
			String routePath = route.getPath();
			if (routePath.startsWith(slash)) {
				routePath = routePath.substring(1);
			}
			if (routePath.endsWith(slash)) {
				routePath = routePath.substring(0, routePath.length() - 1);
			}

			List<Route> parents = new ArrayList<Route>();
			while (route.getExtends() != null) {
				parents.add(route.getExtends());
				route = route.getExtends();
			}

			Collections.reverse(parents);

			StringBuilder builder = new StringBuilder();
			builder.append(slash);
			for (Route parent : parents) {
				String parentRoute = parent.getPath().trim();
				if (parentRoute.startsWith(slash)) {
					parentRoute = parentRoute.substring(1);
				}
				if (parentRoute.endsWith(slash)) {
					parentRoute = parentRoute.substring(0, parentRoute.length() - 1);
				}
				builder.append(parentRoute);
				builder.append(slash);
			}

			builder.append(routePath);
			return builder.toString();
		}
		return ""; //$NON-NLS-1$
	}
}
