/**
 * © Cerema/DSI 2015
 * Contributeurs:
 * Raphaël Roulet raphael.roulet@cerema.fr
 * Alain Charles  alain.charles@cerema.fr
 * <p>
 * Ce logiciel est un programme informatique servant à cartographier et analyser
 * les compétences collectives disponibles au Cerema.  *
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et
 * respectant les principes de diffusion des logiciels libres. Vous pouvez
 * utiliser, modifier et/ou redistribuer ce programme sous les conditions de la
 * licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
 * "http://www.cecill.info".
 * <p>
 * En contrepartie de l'accessibilité au code source et des droits de copie, de
 * modification et de redistribution accordés par cette licence, il n'est offert
 * aux utilisateurs qu'une garantie limitée. Pour les mêmes raisons, seule une
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
 * droits patrimoniaux et les concédants successifs.
 * <p>
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés
 * au chargement, à l'utilisation, à la modification et/ou au développement et à
 * la reproduction du logiciel par l'utilisateur étant donné sa spécificité de
 * logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
 * donc à des développeurs et des professionnels avertis possédant des
 * connaissances informatiques approfondies. Les utilisateurs sont donc invités
 * à charger et tester l'adéquation du logiciel à leurs besoins dans des
 * conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs
 * données et, plus généralement, à l'utiliser et l'exploiter dans les mêmes
 * conditions de sécurité.  *
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes.
 */
package fr.cerema.dsi.commons.repositories;

import java.util.List;

/**
 *
 * @author acharles
 */
public interface IAbstractDao<T> {

	public void create(T entity);

	public T edit(T entity);

	public void remove(T entity);

	public T refresh(T entity);

	public void flush();

	public T find(Object id);

	public List<T> findAll();

	public List<T> findRange(int[] range);

	public int count();

	public void clearL2Cache();

}
