package org.geekpower.common;

import java.io.Serializable;

/**
 * 多元元器件
 * 
 * @author Admin
 *
 */
public class Tuple {

	/**
	 * It is same with Pair, but first element data type is String and provide
	 * friendly function name
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static <T> FieldPair<T> makeFieldPair(final String name, final T value) {
		return new FieldPair<T>(name, value);
	}

	/**
	 * create two value tuple.
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static <A, B> Pair<A, B> makePair(final A v1, final B v2) {
		return new Pair<A, B>(v1, v2);
	}

	/**
	 * create three value tuple
	 * 
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return
	 */
	public static <A, B, C> Triplet<A, B, C> makeTriplet(final A v1, final B v2, final C v3) {
		return new Triplet<A, B, C>(v1, v2, v3);
	}

	public static <A, B, C, D> Quartet<A, B, C, D> makeQuartet(final A v1, final B v2, final C v3, final D v4) {
		return new Quartet<A, B, C, D>(v1, v2, v3, v4);
	}

	public static class FieldPair<T> {
		private String name;
		private T value;

		protected FieldPair(String name, T value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public T getValue() {
			return value;
		}
	}

	public static class Pair<A, B> implements Serializable {
		private static final long serialVersionUID = 1L;
		private A v1;
		private B v2;

		protected Pair(A v1, B v2) {
			this.v1 = v1;
			this.v2 = v2;
		}

		public A getFirst() {
			return v1;
		}

		public B getSecond() {
			return v2;
		}

		public String toString() {
			return "v1=" + v1 + " v2=" + v2;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object anObject) {
			boolean equalObjects = false;

			if (anObject != null && this.getClass() == anObject.getClass()) {
				equalObjects = this.getFirst().equals(((Pair) anObject).getFirst())
						&& this.getSecond().equals(((Pair) anObject).getSecond());
			}

			return equalObjects;
		}

		@Override
		public int hashCode() {
			int hashCodeValue = +(90113 * 223) + this.getFirst().hashCode() + this.getSecond().hashCode();

			return hashCodeValue;
		}
	}

	public static class Triplet<A, B, C> extends Pair<A, B> {
		private static final long serialVersionUID = 1L;
		private C v3;

		protected Triplet(A v1, B v2, C v3) {
			super(v1, v2);
			this.v3 = v3;
		}

		public C getThird() {
			return v3;
		}

		public String toString() {
			return "v1=" + getFirst() + " v2=" + getSecond() + " v3=" + getThird();
		}
	}

	public static class Quartet<A, B, C, D> extends Triplet<A, B, C> {
		private static final long serialVersionUID = 1L;
		private D v4;

		protected Quartet(A v1, B v2, C v3, D v4) {
			super(v1, v2, v3);
			this.v4 = v4;
		}

		public D getForth() {
			return v4;
		}

		public String toString() {
			return "v1=" + getFirst() + " v2=" + getSecond() + " v3=" + getThird() + " v4=" + getForth();
		}
	}
}
