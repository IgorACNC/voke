package br.voke.infraestrutura.compartilhado;

import java.lang.reflect.Field;

public final class DominioReflection {

    private DominioReflection() {
    }

    public static void definirCampo(Object alvo, String nomeCampo, Object valor) {
        try {
            Field campo = encontrarCampo(alvo.getClass(), nomeCampo);
            campo.setAccessible(true);
            campo.set(alvo, valor);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Nao foi possivel reconstruir o campo de dominio: " + nomeCampo, e);
        }
    }

    private static Field encontrarCampo(Class<?> tipo, String nomeCampo) throws NoSuchFieldException {
        Class<?> atual = tipo;
        while (atual != null) {
            try {
                return atual.getDeclaredField(nomeCampo);
            } catch (NoSuchFieldException ignored) {
                atual = atual.getSuperclass();
            }
        }
        throw new NoSuchFieldException(nomeCampo);
    }
}
