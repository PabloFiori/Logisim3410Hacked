/*******************************************************************************
 * This file is part of logisim-evolution.
 *
 *   logisim-evolution is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   logisim-evolution is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with logisim-evolution.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Original code by Carl Burch (http://www.cburch.com), 2011.
 *   Subsequent modifications by :
 *     + Haute École Spécialisée Bernoise
 *       http://www.bfh.ch
 *     + Haute École du paysage, d'ingénierie et d'architecture de Genève
 *       http://hepia.hesge.ch/
 *     + Haute École d'Ingénierie et de Gestion du Canton de Vaud
 *       http://www.heig-vd.ch/
 *   The project is currently maintained by :
 *     + REDS Institute - HEIG-VD
 *       Yverdon-les-Bains, Switzerland
 *       http://reds.heig-vd.ch
 *******************************************************************************/

package com.cburch.logisim.std.gates;

import java.awt.Graphics;
import java.util.ArrayList;

import com.bfh.logisim.settings.Settings;
import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;

class OrGate extends AbstractGate {
	private class OrGateHDLGeneratorFactory extends AbstractGateHDLGenerator {
		@Override
		public ArrayList<String> GetLogicFunction(int nr_of_inputs,
				int bitwidth, boolean is_one_hot, String HDLType) {
			ArrayList<String> Contents = new ArrayList<String>();
			String Preamble = (HDLType.equals(Settings.VHDL) ? "" : "assign ");
			String OrOperation = (HDLType.equals(Settings.VHDL) ? " OR" : " |");
			String AssignOperation = (HDLType.equals(Settings.VHDL) ? " <= "
					: " = ");
			StringBuffer OneLine = new StringBuffer();
			OneLine.append("   " + Preamble + "Result" + AssignOperation);
			int TabWidth = OneLine.length();
			boolean first = true;
			for (int i = 0; i < nr_of_inputs; i++) {
				if (!first) {
					OneLine.append(OrOperation);
					Contents.add(OneLine.toString());
					OneLine.setLength(0);
					while (OneLine.length() < TabWidth) {
						OneLine.append(" ");
					}
				} else {
					first = false;
				}
				OneLine.append("s_real_input_" + Integer.toString(i + 1));
			}
			OneLine.append(";");
			Contents.add(OneLine.toString());
			Contents.add("");
			return Contents;
		}
	}

	public static OrGate FACTORY = new OrGate();

	private OrGate() {
		super("OR Gate", Strings.getter("orGateComponent"));
		setRectangularLabel("\u2265" + "1");
		setIconNames("orGate.gif", "orGateRect.gif", "dinOrGate.gif");
		setPaintInputLines(true);
	}

	@Override
	protected Expression computeExpression(Expression[] inputs, int numInputs) {
		Expression ret = inputs[0];
		for (int i = 1; i < numInputs; i++) {
			ret = Expressions.or(ret, inputs[i]);
		}
		return ret;
	}

	@Override
	protected Value computeOutput(Value[] inputs, int numInputs,
			InstanceState state) {
		return GateFunctions.computeOr(inputs, numInputs);
	}

	@Override
	protected Value getIdentity() {
		return Value.FALSE;
	}

	@Override
	public boolean HDLSupportedComponent(String HDLIdentifier,
			AttributeSet attrs) {
		if (MyHDLGenerator == null)
			MyHDLGenerator = new OrGateHDLGeneratorFactory();
		return MyHDLGenerator.HDLTargetSupported(HDLIdentifier, attrs);
	}

	@Override
	protected void paintDinShape(InstancePainter painter, int width,
			int height, int inputs) {
		PainterDin.paintOr(painter, width, height, false);
	}

	@Override
	public void paintIconShaped(InstancePainter painter) {
		Graphics g = painter.getGraphics();
		GraphicsUtil.drawCenteredArc(g, 0, -5, 22, -90, 53);
		GraphicsUtil.drawCenteredArc(g, 0, 23, 22, 90, -53);
		GraphicsUtil.drawCenteredArc(g, -12, 9, 16, -30, 60);
	}

	@Override
	protected void paintShape(InstancePainter painter, int width, int height) {
		PainterShaped.paintOr(painter, width, height);
	}

	@Override
	protected boolean shouldRepairWire(Instance instance, WireRepairData data) {
		boolean ret = !data.getPoint().equals(instance.getLocation());
		return ret;
	}

}
