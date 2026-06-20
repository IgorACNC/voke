package br.voke.infraestrutura.fidelidade.carteira;

import org.springframework.stereotype.Repository;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtual;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualId;
import br.voke.dominio.fidelidade.carteira.CarteiraVirtualRepositorio;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CarteiraVirtualRepositorioJpa implements CarteiraVirtualRepositorio {

    private final SpringCarteiraVirtualRepository repository;

    public CarteiraVirtualRepositorioJpa(SpringCarteiraVirtualRepository repository) {
        this.repository = repository;
    }

    public void salvar(CarteiraVirtual carteira) {
        repository.save(CarteiraVirtualJpaMapper.paraJpa(carteira));
    }

    public Optional<CarteiraVirtual> buscarPorId(CarteiraVirtualId id) {
        return repository.findById(id.getValor()).map(CarteiraVirtualJpaMapper::paraDominio);
    }

    public Optional<CarteiraVirtual> buscarPorParticipanteId(UUID participanteId) {
        return repository.findByParticipanteId(participanteId).map(CarteiraVirtualJpaMapper::paraDominio);
    }

    public void resetarLimitesDiarios() {
        repository.resetarLimitesDiarios();
    }
}