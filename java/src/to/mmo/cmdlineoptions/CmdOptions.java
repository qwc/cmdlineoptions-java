package to.mmo.cmdlineoptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Written by Marcel M. Otte, (c) 2013 For use under the BSD 2-clause License,
 * or in other words: Do what you want with it as long as you leave all
 * copyright notices where they are and don't bother me when you break your pc.
 * :)
 */

public class CmdOptions {

	private static CmdOptions instance;

	private static String optionChar;

	private boolean combineSwitches;

	public static class Option {
		private String name;
		private ArrayList<String> cmd;
		private ArrayList<String> cmdLong;
		private String description;
		private ArrayList<String> defaultParameter;

		private ArrayList<String> possibleParams;
		private boolean set;
		private boolean required;
		private ArrayList<String> values;
		private int maxParameters, minParameters;
		private ArrayList<String> examples;
		private int stepSizeParameters;

		public ArrayList<String> getValues() {
			return values;
		}

		public ArrayList<String> getDefaultParameter() {
			return defaultParameter;
		}

		public Option() {
			values = new ArrayList<String>();
			cmd = new ArrayList<String>();
			cmdLong = new ArrayList<String>();
			defaultParameter = new ArrayList<String>();
			possibleParams = new ArrayList<String>();
			examples = new ArrayList<String>();
		}

		public Option addCommand(String cmd) {
			if (cmd.contains(optionChar)) {
				cmd = cmd.replaceAll(optionChar, "");
			}
			if (cmd.length() > 1) {
				throw new IllegalArgumentException(
						"Command longer than 1 character, which is not allowed. Use 'addLongCommand()' instead!");
			}
			this.cmd.add(cmd);
			return this;
		}

		public Option addLongCommand(String cmd) {
			if (cmd.contains(optionChar))
				cmd.replaceAll(optionChar, "");
			this.cmdLong.add(cmd);
			return this;
		}

		public Option addDefaultParameter(String d) {
			this.defaultParameter.add(d);
			return this;
		}

		public Option addPossibleParameter(String p) {
			this.possibleParams.add(p);
			return this;
		}

		public Option addValue(String value) {
			this.values.add(value);
			return this;
		}

		public String getName() {
			return name;
		}

		public Option setName(String name) {
			this.name = name;
			return this;
		}

		public Option setParameterCount(int min, int max) {
			return this.setParameterCount(min, max, 0);
		}

		public Option setParameterCount(int min, int max, int step) {
			this.minParameters = min;
			this.maxParameters = max;
			this.stepSizeParameters = step;
			return this;
		}

		public String getDescription() {
			return description;
		}

		public Option setDescription(String description) {
			this.description = description;
			return this;
		}

		public Option setRequired(boolean required) {
			this.required = true;
			return this;
		}

		public boolean isSet() {
			return set;
		}

		public boolean valuesContains(String value) {
			return values.contains(value);
		}

		public int getIndexOf(String value) {
			return values.indexOf(value);
		}

		public Option setSet(boolean set) {
			this.set = set;
			return this;
		}

		public Option addExample(String example) {
			this.examples.add(example);
			return this;
		}

		public String toString() {
			String ret = name + " (";
			for (String s : cmd) {
				ret += s + ", ";
			}
			ret += ")"
					+ (defaultParameter != null ? ": default="
							+ defaultParameter : "")
					+ (description != null ? "\n\t\t" + description : "");
			if (possibleParams != null) {
				boolean start = true;
				ret += "\n\t\t(Possible parameters: ";
				for (String s : possibleParams) {
					ret += (start ? "" : ", ") + s;
					start = false;
				}
				ret += ")";
			}
			if (set) {
				ret += "\n\t\t--> current Setting: ";
				if (values.size() > 0) {
					boolean start = true;
					for (String s : values) {
						ret += (start ? "" : ",") + s;
						start = false;
					}
				} else {
					ret += "true";
				}
				// ret += "\n";
			}
			return ret;
		}
	}

	private HashMap<String, Option> options;

	private CmdOptions() {
		optionChar = "-";
		this.setSwitchCombination(false);
		options = new HashMap<String, Option>();
		this.createOption("help")
				.setDescription(
						"Show all possible options and their parameters.")
				.addCommand("--help").addCommand("-h").addCommand("-?");
	}

	public static CmdOptions i() {
		if (instance == null) {
			instance = new CmdOptions();
		}
		return instance;
	}

	public void setSwitchCombination(boolean on) {
		this.combineSwitches = on;
	}

	public void setOptionCharacter(String c) {
		this.optionChar = c;
	}

	public void setHelpGeneration(boolean on) {
		if (!on) {
			options.remove("help");
		}
	}

	public Option createOption(String name) {
		Option o = new Option();
		o.setName(name);
		this.options.put(name, o);
		return o;
	}

	public Option getBareOption(String name) {
		return options.get(name);
	}

	public String[] get(String name) {
		return getOption(name);
	}

	public String[] getOption(String name) {
		if (options.get(name).values.size() > 0)
			return options.get(name).values.toArray(new String[0]);
		else if (options.get(name).defaultParameter != null)
			return options.get(name).getValues().toArray(new String[0]);
		return null;
	}

	public List<String> getValuesAsList(String name) {
		if (options.get(name).getValues().size() > 0) {
			return options.get(name).getValues();
		}
		return null;
	}

	public Integer[] getOptionAsInt(String name) {
		if (options.get(name).values.size() > 0) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (String o : options.get(name).values) {
				list.add(Integer.parseInt(o));
			}
			return list.toArray(new Integer[0]);
		} else if (options.get(name).getDefaultParameter().size() > 0) {
			ArrayList<Integer> list = new ArrayList<Integer>();
			for (String o : options.get(name).getDefaultParameter()) {
				list.add(Integer.parseInt(o));
			}
			return list.toArray(new Integer[0]);
		}
		return null;
	}

	public Integer getOptionAsInt(String name, int index) {
		Integer[] array = getOptionAsInt(name);
		if (index >= 0 && index < array.length) {
			return array[index];
		}
		return null;
	}

	public Double[] getOptionAsDouble(String name) {
		if (options.get(name).values.size() > 0) {
			ArrayList<Double> list = new ArrayList<Double>();
			for (String o : options.get(name).values) {
				list.add(Double.parseDouble(o));
			}
			return list.toArray(new Double[0]);
		} else if (options.get(name).getDefaultParameter().size() > 0) {
			ArrayList<Double> list = new ArrayList<Double>();
			for (String o : options.get(name).getDefaultParameter()) {
				list.add(Double.parseDouble(o));
			}
			return list.toArray(new Double[0]);
		}
		return null;
	}

	public Double getOptionAsDouble(String name, int index) {
		Double[] array = getOptionAsDouble(name);
		if (index >= 0 && index < array.length) {
			return array[index];
		}
		return null;
	}

	public boolean isSet(String option) {
		return options.get(option).set;
	}

	public boolean isSet(String option, String parameter) {
		return this.getValuesAsList(option).contains(parameter);
	}

	public String toString(boolean help) {
		StringBuilder b = new StringBuilder();
		if (help) {
			b.append("Possible options:\n");
		}
		b.append("-options\n");
		Option[] vars = options.values().toArray(new Option[0]);
		Arrays.sort(vars, new Comparator<Option>() {
			@Override
			public int compare(Option o1, Option o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		for (Option o : vars) {
			b.append("\t").append(o.toString()).append("\n");
		}
		b.append("/options\n");
		return b.toString();
	}

	private Integer[] getIndices(String[] args) {
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < args.length; ++i) {
			if (args[i].startsWith(optionChar)) {
				indices.add(i);
			}
		}
		return indices.toArray(new Integer[0]);
	}

	private boolean optionExists(String option) {
		for (Option o : this.options.values()) {
			for (String s : o.cmd) {
				if (option.equals(s)) {
					return true;
				}
			}
			for (String s : o.cmdLong) {
				if (option.equals(s)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean switchExists(char c) {
		for (Option op : this.options.values()) {
			for (String s : op.cmd) {
				if (s.toCharArray()[0] == c) {
					return true;
				}
			}
		}
		return false;
	}

	public void parse(String[] args) {
		int exit = 0;
		// get indices
		Integer[] indices = getIndices(args);
		// check for correct options
		boolean ok = true;
		for (Integer i : indices) {
			String o = args[i].replaceAll(optionChar, "");
			if (!optionExists(o)) {
				if (this.combineSwitches) {
					for (char c : o.toCharArray()) {
						if (!switchExists(c)) {
							System.err.println("Unrecognized option '" + o
									+ "'");
							ok = false;
						}
					}
				} else {
					System.err.println("Unrecognized option '" + o + "'");
					ok = false;
				}
			}
		}
		// quit if there are unknown options
		if (!ok) {
			exit = 1;
		}
		// now parse
		for (int a = 0; a < indices.length; ++a) {
			String o = args[indices[a]].replaceAll(optionChar, "");
			// the option is set!
			this.getBareOption(o).setSet(true);
			// are there parameters?
			if (indices[a] < args.length - 1 && a < indices.length - 1
					&& indices[a + 1] - indices[a] > 1) {
				// parameters between options
				for (int b = indices[a] + 1; b < indices[a + 1]; ++b) {
					this.getBareOption(o).getValues().add(args[b]);
				}
			} else if (a == indices.length - 1 && args.length - 1 > indices[a]) {
				// parameters at the last option
				for (int b = indices[a] + 1; b < args.length; ++b) {
					this.getBareOption(o).getValues().add(args[b]);
				}
			}
		}

		// check for possible parameters
		for (Option o : options.values()) {
			for (String s : o.getValues()) {
				if (!o.possibleParams.contains(s)) {
					System.err.println("Parameter \"" + s + "\" for Option \""
							+ o.name + "\" not allowed!");
					ok = false;
				}
			}
		}
		if (!ok) {
			exit = 2;
		}

		// check parameter counts
		for (Option o : options.values()) {
			if (o.getValues().size() < o.minParameters
					|| o.getValues().size() > o.maxParameters
					|| o.stepSizeParameters != 0
					&& o.getValues().size() % o.stepSizeParameters != 0) {
				System.err.println(o.name
						+ ": Parameter count not correct! Check help.");
				exit = 3;
			}
		}

		// set default for that options that aren't set
		for (Option o : options.values()) {
			if (!o.set && o.required) {
				System.err
						.println(o.name
								+ " ("
								+ o.getName()
								+ "): has no default parameter and has to be set on commandline!");
				exit = 4;
			}
			if (!o.set && o.defaultParameter.size() != 0)
				o.values.addAll(o.defaultParameter);
		}
		if (options.get("help").set) {
			System.out.println(this.toString(true));
			System.exit(exit);
		}
		System.out.println(this.toString(false));
	}
}
